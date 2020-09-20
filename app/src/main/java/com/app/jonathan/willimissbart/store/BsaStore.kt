package com.app.jonathan.willimissbart.store

import com.app.jonathan.willimissbart.api.BartService
import com.app.jonathan.willimissbart.apimodels.bsa.ApiBsa
import com.app.jonathan.willimissbart.apimodels.bsa.ApiBsaRoot
import com.app.jonathanchiou.willimissbart.db.dao.BsaDao
import com.app.jonathanchiou.willimissbart.db.models.Bsa
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BsaStore @Inject constructor(
    private val bartService: BartService,
    private val bsaDao: BsaDao
) {

    fun getBsas(): Single<List<Bsa>> {
        return bartService
            .getBsas()
            .map(ApiBsaRoot::bsa)
            .map { apiBsas ->
                apiBsas.filter { apiBsa -> apiBsa.id != null }
                    .map(ApiBsa::toDbModel)
            }
            .subscribeOn(Schedulers.io())
            .flatMap { bsas ->
                bsaDao.insert(bsas)
                    .andThen(Single.just(bsas))
                    .subscribeOn(Schedulers.io())
            }
    }
}
