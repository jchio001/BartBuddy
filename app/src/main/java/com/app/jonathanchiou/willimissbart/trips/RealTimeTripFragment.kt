package com.app.jonathanchiou.willimissbart.trips

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.app.jonathanchiou.willimissbart.MainActivity
import com.app.jonathanchiou.willimissbart.R
import com.app.jonathanchiou.willimissbart.api.ApiClient
import com.app.jonathanchiou.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.trips.TripManager.StationListener
import com.app.jonathanchiou.willimissbart.utils.models.State

class RealTimeTripViewModelFactory(private val bartService: BartService):
    ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == RealTimeTripViewModel::class.java) {
            return RealTimeTripViewModel(bartService) as T
        }

        throw IllegalStateException("Invalid class!")
    }
}

class RealTimeTripFragment: Fragment() {

    @BindView(R.id.edit_icon)
    lateinit var editIcon: ImageView

    @BindView(R.id.trip_information_textview)
    lateinit var tripInformationTextView: TextView

    @BindView(R.id.container)
    lateinit var container: FrameLayout

    lateinit var tripManager: TripManager

    lateinit var realTimeTripViewModel: RealTimeTripViewModel

    val realTimeTripAdapter = RealTimeTripAdapter()

    private val stationListener = object: StationListener {
        override fun onTripStationChanged(originAbbreviation: String?, destinationAbbreviation: String?) {
            tripInformationTextView.text = "$originAbbreviation to $destinationAbbreviation"
            realTimeTripViewModel.requestTrip(originAbbreviation!!, destinationAbbreviation!!)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_real_time_trips, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)

        realTimeTripViewModel = ViewModelProviders
            .of(this,
                RealTimeTripViewModelFactory(
                    ApiClient.INSTANCE
                        .bartService))
            .get(RealTimeTripViewModel::class.java)
        realTimeTripViewModel.realTimeTripLiveData
            .observe(viewLifecycleOwner, Observer {
                if (it.state == State.PENDING) {
                    if (container.childCount== 0
                        || container.getChildAt(0).id != R.id.recyclerview) {
                        val progressBar = LayoutInflater.from(context)
                            .inflate(R.layout.layout_progress_bar, container, false)

                        container.removeAllViews()
                        container.addView(progressBar)
                    }
                } else if (it.state == State.DONE) {
                    if (container.childCount == 0
                        || container.getChildAt(0).id != R.id.recyclerview) {
                        val recyclerView = LayoutInflater.from(context)
                            .inflate(R.layout.layout_model_recyclerview, container, false)
                            as RecyclerView
                        recyclerView.adapter = realTimeTripAdapter
                        recyclerView.layoutManager = LinearLayoutManager(context)

                        container.removeAllViews()
                        container.addView(recyclerView)
                    }

                    realTimeTripAdapter.setTrips(it.data!!)
                }
            })

        tripManager = (activity as MainActivity).tripManager
        tripManager.addListener(stationListener)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            tripManager.addListener(stationListener)
        } else {
            tripManager.removeListener(stationListener)
        }
    }

    @OnClick(R.id.edit_icon)
    fun onEditIconClicked() {
        val tripSelectionFragment = TripSelectionFragment()

        fragmentManager!!.also {
         it.beginTransaction()
             .hide(it.fragments[0])
             .add(R.id.parent, tripSelectionFragment)
             .show(tripSelectionFragment)
             .addToBackStack("TripSelectionFragment")
             .commit()
        }
    }
}