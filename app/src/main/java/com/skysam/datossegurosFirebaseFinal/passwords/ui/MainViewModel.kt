package com.skysam.datossegurosFirebaseFinal.passwords.ui

import androidx.lifecycle.*
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel
import com.skysam.datossegurosFirebaseFinal.database.firebase.Firestore
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    /*private val _passwords = MutableLiveData<List<PasswordsModel>>()
    val passwords: LiveData<List<PasswordsModel>> = _passwords*/
    val passwords: LiveData<List<PasswordsModel>> = Firestore.getPasswords().asLiveData()

    init {
        viewModelScope.launch {
            //_passwords.value = Firestore.getPasswords()

        }
    }
}