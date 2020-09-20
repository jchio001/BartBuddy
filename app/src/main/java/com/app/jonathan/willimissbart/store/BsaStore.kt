package com.app.jonathan.willimissbart.store

import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.apimodels.bsa.ApiBsa
import com.app.jonathan.willimissbart.apimodels.bsa.ApiBsaRoot
import com.app.jonathanchiou.willimissbart.db.dao.BsaDao
import com.app.jonathanchiou.willimissbart.db.models.Bsa
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BsaStore @Inject constructor(
    private val bartService: BartService,
    private val bsaDao: BsaDao
) {
    fun poll(): Completable {
        return bartService.getBsas()
            .map(ApiBsaRoot::bsa)
            .map { apiBsas ->
                apiBsas.filter { apiBsa -> apiBsa.id != null }
                    .map(ApiBsa::toDbModel)
            }
            .repeatWhen { completed ->
                completed.delay(15, TimeUnit.SECONDS)
            }
            .retryWhen { error ->
                error.delay(15, TimeUnit.SECONDS)
            }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { bsas ->
                bsaDao.insert(bsas)
                    .subscribeOn(Schedulers.io())
            }
    }

    fun stream(): Observable<List<Bsa>> {
        return bsaDao.getActiveBsas()
            .subscribeOn(Schedulers.io())
    }
}
