package com.app.jonathan.willimissbart.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    indices = [Index(value = ["id"], unique = true)]
)
class Bsa(
    @PrimaryKey val id: String,
    val description: String,
    val postedDate: Date,
    val expirationDate: Date,
)
