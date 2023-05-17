package com.example.petside.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

/*    @Update(entity = UserEntity::class)
    fun updateApiKey(newKey: ApiKeyUpdate)*/

    @Query("SELECT * FROM user WHERE id = 0")
    fun getUser(): LiveData<UserEntity>

    @Query("SELECT api_key FROM user")
    fun getApiKey(): String
}