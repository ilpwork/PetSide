package com.example.petside.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.AuthRequest
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.launch
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

class AuthViewModel : ViewModel() {

    lateinit var retrofitService: RetrofitService
    lateinit var dao: Dao
    var newUser: UserEntity = UserEntity(
        email = "",
        description = "",
        api_key = ""
    )

    val buttonEnabled = MutableLiveData(false)
    var loading = MutableLiveData(false)

    fun checkEmailAndDescription() {
        buttonEnabled.value =
            android.util.Patterns.EMAIL_ADDRESS.matcher(newUser.email)
                .matches() && newUser.description.isNotEmpty()
    }

    fun auth(onSuccess: () -> Unit, onError: (e: HttpException) -> Unit) {
        viewModelScope.launch {
            try {
                retrofitService.auth(
                    AuthRequest(
                        newUser.email,
                        newUser.description
                    )
                )
                dao.insertUser(newUser)
            } catch (e: HttpException) {
                onError(e)
                throw CancellationException()
            }
        }.invokeOnCompletion {
            if (it === null) {
                onSuccess()
            }
        }
    }
}