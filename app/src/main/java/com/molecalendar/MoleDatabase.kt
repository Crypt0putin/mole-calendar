package com.molecalendar

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Mole::class], version = 1, exportSchema = false)
abstract class MoleDatabase : RoomDatabase() {
    abstract fun moleDao(): MoleDao

    companion object {
        @Volatile
        private var INSTANCE: MoleDatabase? = null

        fun getDatabase(context: Context): MoleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoleDatabase::class.java,
                    "mole_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
