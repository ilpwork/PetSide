package com.example.petside.db

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity)

/*    @Update(entity = UserEntity::class)
    fun updateApiKey(newKey: ApiKeyUpdate)*/

    @Query("SELECT * FROM user WHERE id = 0")
    fun getUser(): UserEntity?

    @Query("SELECT api_key FROM user")
    fun getApiKey(): String
}