package com.example.petside.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ApiKeyViewModel : ViewModel() {

    lateinit var retrofitService: RetrofitService
    lateinit var dao: Dao
    var newUser = UserEntity(email = "", description = "", api_key = "")
    var loading = MutableLiveData(false)

    fun checkKey(onSuccess: () -> Unit, onError: (e: HttpException) -> Unit) {
        viewModelScope.launch {
            try {
                loading.value = true
                retrofitService.getFavourites(newUser.api_key)
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertUser(newUser)
                }
            } catch (e: HttpException) {
                onError(e)
                throw CancellationException()
            }
        }.invokeOnCompletion {
            if (it === null) {
                onSuccess()
            }
            loading.value = false
        }
    }

}