package com.example.sellcar.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sellcar.data.User

class UserViewModel : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    fun setUser(user: User?) {
        _user.value = user
    }

    fun clearUser() {
        _user.value = null
    }
}