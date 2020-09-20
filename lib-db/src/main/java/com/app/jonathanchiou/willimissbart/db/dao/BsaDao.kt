package com.app.jonathanchiou.willimissbart.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.jonathanchiou.willimissbart.db.models.Bsa
import io.reactivex.Completable

@Dao
interface BsaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(bsas: List<Bsa>) : Completable
}
