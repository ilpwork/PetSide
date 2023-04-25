package com.example.petside.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class], version = 1)
abstract class MainDb : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        fun getMainDb(context: Context): MainDb {
            return Room.databaseBuilder(context.applicationContext, MainDb::class.java, "cat.db")
                .build()
        }
    }
}