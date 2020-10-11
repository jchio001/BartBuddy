package com.app.jonathan.willimissbart.store

import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsa
import com.app.jonathanchiou.willimissbart.api.models.bsa.ApiBsaRoot
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BsaStore @Inject constructor(
    private val bartService: BartService,
) {

    fun stream(): Observable<List<ApiBsa>> {
        return bartService.getBsas()
            .repeatWhen { completed ->
                completed.delay(15, TimeUnit.SECONDS)
            }
            .retryWhen { error ->
                error.delay(15, TimeUnit.SECONDS)
            }
            .map { apiBsas ->
                apiBsas.bsa.map { apiBsa ->
                    if (apiBsa.posted == null) {
                        apiBsa.copy(posted = Date())
                    } else {
                        apiBsa
                    }
                }
            }
            .subscribeOn(Schedulers.io())
    }
}
