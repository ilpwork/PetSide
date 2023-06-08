package com.example.petside.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.example.domain.model.User

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

/*    @Update(entity = User::class)
    fun updateApiKey(newKey: ApiKeyUpdate)*/

    @Query("SELECT * FROM user WHERE id = 0")
    fun getUser(): LiveData<User>

    @Query("SELECT api_key FROM user")
    fun getApiKey(): String
}