package com.camelcc.overcooked

import androidx.lifecycle.ViewModel

class ProfileViewModel : ViewModel() {
    val userFlow = LoginRepository.userIdFlow
}