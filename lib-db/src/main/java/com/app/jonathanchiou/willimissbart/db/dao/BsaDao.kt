package com.app.jonathanchiou.willimissbart.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.jonathanchiou.willimissbart.db.models.Bsa
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*

@Dao
interface BsaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bsas: List<Bsa>) : Completable

    @Query(
        """
            Select * from Bsa
            where
            expirationDate > :date
            order by postedDate desc
        """
    )
    fun getActiveBsas(date: Date = Date()) : Observable<List<Bsa>>
}
