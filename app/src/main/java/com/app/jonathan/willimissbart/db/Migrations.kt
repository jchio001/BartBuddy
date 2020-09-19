package com.app.jonathan.willimissbart.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    fun get() = arrayOf<Migration>(
        object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `Bsa` (
                            `id` TEXT NOT NULL, 
                            `description` TEXT NOT NULL, 
                            `postedDate` INTEGER NOT NULL, 
                            `expirationDate` INTEGER NOT NULL, 
                            PRIMARY KEY(`id`)
                        )
                        """.trimIndent()
                    )
                    execSQL(
                        """
                            CREATE UNIQUE INDEX IF NOT EXISTS `index_Bsa_id` ON `BSA` (`id`)
                        """.trimIndent()
                    )
                }
            }
        }
    )
}
