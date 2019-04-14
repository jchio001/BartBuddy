package com.app.jonathanchiou.willimissbart.trips

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.api.BartResponseWrapper
import com.app.jonathanchiou.willimissbart.utils.models.State
import com.app.jonathanchiou.willimissbart.utils.models.UiModel
import com.app.jonathanchiou.willimissbart.utils.models.responseToTerminalUiModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import retrofit2.Response

class TripRequestEvent(val originAbbreviation: String,
                       val destinationAbbreviation: String)

class TripEtdPair(val originResponse: Response<BartResponseWrapper<EtdRoot>>,
                  val destinationResponse: Response<BartResponseWrapper<EtdRoot>>)

// TODO: Make sure this spaghetti ball works.
class TripViewModel(application: Application): AndroidViewModel(application) {

    val tripLiveData = MutableLiveData<UiModel<RealTimeTrip>>()

    private val tripEventSubject = PublishSubject.create<TripRequestEvent>()

    init {
        tripEventSubject
            .switchMap { tripRequestEvent ->
                ApiClient.INSTANCE.bartService.let { bartService ->
                   bartService.getDepartures(
                       tripRequestEvent.originAbbreviation,
                       tripRequestEvent.destinationAbbreviation)
                       .subscribeOn(Schedulers.io())
                       .flatMap {
                           Observable
                               .zip(
                                   bartService.getRealTimeEstimates(tripRequestEvent.originAbbreviation),
                                   bartService.getRealTimeEstimates(tripRequestEvent.destinationAbbreviation),
                                   object: BiFunction<
                                       Response<BartResponseWrapper<EtdRoot>>,
                                       Response<BartResponseWrapper<EtdRoot>>,
                                       Response<TripEtdPair>> {

                                       // Assuming that if both calls fail, they fail for the same reason!
                                       override fun apply(t1: Response<BartResponseWrapper<EtdRoot>>,
                                                          t2: Response<BartResponseWrapper<EtdRoot>>):
                                           Response<TripEtdPair> {
                                           if (t1.isSuccessful && t2.isSuccessful) {
                                               return Response
                                                   .success(
                                                       TripEtdPair(t1, t2))
                                           } else {
                                              return Response.error(t1.code(), t1.errorBody())
                                           }
                                       }
                                   })
                               .responseToTerminalUiModel()
                       }
                       .map { tripEtdPairUiModel ->
                           if (tripEtdPairUiModel.state == State.DONE) {
                               val realTimeTrip = tripEtdPairUiModel.data!!.let {
                                   RealTimeTrip(
                                       originAbbreviation = tripRequestEvent.originAbbreviation,
                                       destinationAbbreviation = tripRequestEvent.destinationAbbreviation,
                                       originEtds = it.originResponse.body()!!.root.etdStation[0].etds,
                                       destinationEtds = it.destinationResponse.body()!!.root.etdStation[0].etds)
                               }

                               return@map UiModel(
                                   state = State.DONE,
                                   data = realTimeTrip,
                                   statusCode = tripEtdPairUiModel.statusCode)
                           } else {
                               tripEtdPairUiModel.let {
                                   UiModel<RealTimeTrip>(
                                       state = it.state,
                                       statusCode = it.statusCode,
                                       error = it.error)
                               }
                           }
                       }
                       .subscribeOn(Schedulers.io())
                       .observeOn(AndroidSchedulers.mainThread())
                       .startWith(
                           UiModel(
                               state = State.PENDING))
                }
            }
            .subscribe(tripLiveData::postValue)
    }
}