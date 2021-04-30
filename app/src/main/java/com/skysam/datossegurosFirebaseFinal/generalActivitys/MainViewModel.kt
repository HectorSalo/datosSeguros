package com.skysam.datossegurosFirebaseFinal.generalActivitys

import androidx.lifecycle.*
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel
import com.skysam.datossegurosFirebaseFinal.common.model.CardModel
import com.skysam.datossegurosFirebaseFinal.common.model.NoteModel
import com.skysam.datossegurosFirebaseFinal.database.firebase.Firestore
import com.skysam.datossegurosFirebaseFinal.database.room.Room
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password

class MainViewModel : ViewModel() {
    val passwordsFirestore: LiveData<List<Password>> = Firestore.getPasswords().asLiveData()
    val accountsFirestore: LiveData<List<AccountModel>> = Firestore.getAccounts().asLiveData()
    val cardsFirestore: LiveData<List<CardModel>> = Firestore.getCards().asLiveData()
    val notesFirestore: LiveData<List<NoteModel>> = Firestore.getNotes().asLiveData()

    val passwordsRoom: LiveData<MutableList<Password>> = Room.getPasswords().asLiveData()
    val accountsRoom: LiveData<MutableList<Account>> = Room.getAccounts().asLiveData()
    val cardsRoom: LiveData<MutableList<Card>> = Room.getCards().asLiveData()
    val notesRoom: LiveData<MutableList<Note>> = Room.getNotes().asLiveData()
}