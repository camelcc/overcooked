package com.camelcc.overcooked

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LoginState { None, Logining, LoginSuccessful, LoginFailed }
class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow(LoginState.None)
    val loginState = _loginState.asStateFlow()

    fun login(username: String, pwd: String) {
        viewModelScope.launch {
            _loginState.update { LoginState.Logining }
            delay(2000)
            LoginRepository.userLogin()
            _loginState.update { LoginState.LoginSuccessful }
        }
    }
}