package com.myquill.app

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Entry::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun entryDao(): EntryDao
    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myquill.db"
                ).fallbackToDestructiveMigration().build().also { instance = it }
            }
        }
    }
}