package com.skysam.datossegurosFirebaseFinal.generalActivitys

import androidx.lifecycle.*
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel
import com.skysam.datossegurosFirebaseFinal.common.model.CardModel
import com.skysam.datossegurosFirebaseFinal.common.model.NoteModel
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel
import com.skysam.datossegurosFirebaseFinal.database.firebase.Firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val passwords: LiveData<List<PasswordsModel>> = Firestore.getPasswords().asLiveData()
    val accounts: LiveData<List<AccountModel>> = Firestore.getAccounts().asLiveData()
    val cards: LiveData<List<CardModel>> = Firestore.getCards().asLiveData()
    val notes: LiveData<List<NoteModel>> = Firestore.getNotes().asLiveData()
}