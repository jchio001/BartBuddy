package com.app.jonathan.willimissbart.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import io.reactivex.Completable

@Dao
interface BsaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bsas: List<Bsa>) : Completable
}
