package com.example.petside.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.petside.data.db.UserEntity
import com.example.petside.data.repository.UserRepository
import com.example.petside.data.retrofit.AuthRequest
import com.example.petside.data.retrofit.RetrofitService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

data class AuthUiState(
    val isLoading: Boolean = false,
    val buttonEnabled: Boolean = false,
    val errorMessage: String? = null,
    val isUserLoggedIn: Boolean = false
)

class AuthViewModel : ViewModel() {

    @Inject
    lateinit var retrofitService: RetrofitService

    @Inject
    lateinit var userRepository: UserRepository

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    lateinit var user: LiveData<UserEntity>
    var newUser = UserEntity()

    fun getUser() {
        viewModelScope.launch {
            user = userRepository.getUser()
        }
    }

    fun updateEmail(email: String) {
        newUser.email = email
        _uiState.update { currentUiState ->
            currentUiState.copy(
                buttonEnabled = Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches() && newUser.description.isNotEmpty()
            )
        }
    }

    fun updateDescription(description: String) {
        newUser.description = description
        _uiState.update { currentUiState ->
            currentUiState.copy(
                buttonEnabled = Patterns.EMAIL_ADDRESS.matcher(newUser.email)
                    .matches() && description.isNotEmpty()
            )
        }
    }

    fun clearError() {
        _uiState.update { currentUiState ->
            currentUiState.copy(errorMessage = null)
        }
    }

    fun auth() {
        _uiState.update { currentUiState ->
            currentUiState.copy(isLoading = true)
        }
        viewModelScope.launch {
            try {
                retrofitService.auth(
                    AuthRequest(
                        newUser.email,
                        newUser.description
                    )
                )
                CoroutineScope(Dispatchers.IO).launch {
                    userRepository.saveUser(newUser)
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