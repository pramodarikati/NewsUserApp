package com.example.newsuserapp.ui.theme.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsuserapp.data.User
import com.example.newsuserapp.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableLiveData<User?>()
    val authState: LiveData<User?> get() = _authState

    fun signInWithGoogle(idToken: String) {
        authRepository.signInWithGoogle(idToken) { user ->
            _authState.value = user
        }
    }
}
