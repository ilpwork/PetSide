package com.example.petside.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.db.Dao
import com.example.petside.db.UserEntity
import com.example.petside.retrofit.RetrofitService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

data class ApiKeyUiState(
    val isLoading: Boolean = false,
    val buttonEnabled: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false
)

class ApiKeyViewModel : ViewModel() {

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var dao: Dao

    private val _uiState = MutableStateFlow(ApiKeyUiState())
    val uiState: StateFlow<ApiKeyUiState> = _uiState.asStateFlow()

    lateinit var user: LiveData<UserEntity>
    var newUser = UserEntity()
    fun getUser() {
        viewModelScope.launch {
            user = dao.getUser()
        }
    }

    fun updateKey(apiKey: String) {
        newUser.api_key = apiKey
        _uiState.update { currentUiState ->
            currentUiState.copy(
                buttonEnabled = apiKey.isNotEmpty()
            )
        }
    }

    fun clearError() {
        _uiState.update { currentUiState ->
            currentUiState.copy(errorMessage = null)
        }
    }

    fun checkKey() {
        _uiState.update { uiState -> uiState.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                retrofitService.getFavourites(newUser.api_key)
                CoroutineScope(Dispatchers.IO).launch {
                    dao.insertUser(newUser)
                }
            } catch (e: HttpException) {
                throw CancellationException(e.message())
            }
        }.invokeOnCompletion {
            _uiState.update { currentUiState ->
                currentUiState.copy(
                    isLoading = false,
                    isUserLoggedIn = it === null,
                    errorMessage = it?.message
                )
            }
        }
    }

}