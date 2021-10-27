package com.skysam.datossegurosFirebaseFinal.generalActivitys

import androidx.lifecycle.*
import com.skysam.datossegurosFirebaseFinal.database.repositories.FirestoreRepository
import com.skysam.datossegurosFirebaseFinal.database.room.Room
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password

class MainViewModel : ViewModel() {
    val passwordsFirestore: LiveData<List<Password>> = FirestoreRepository.getPasswords().asLiveData()
    val accountsFirestore: LiveData<List<Account>> = FirestoreRepository.getAccounts().asLiveData()
    val cardsFirestore: LiveData<List<Card>> = FirestoreRepository.getCards().asLiveData()
    val notesFirestore: LiveData<List<Note>> = FirestoreRepository.getNotes().asLiveData()

    val passwordsRoom: LiveData<MutableList<Password>> = Room.getPasswords().asLiveData()
    val accountsRoom: LiveData<MutableList<Account>> = Room.getAccounts().asLiveData()
    val cardsRoom: LiveData<MutableList<Card>> = Room.getCards().asLiveData()
    val notesRoom: LiveData<MutableList<Note>> = Room.getNotes().asLiveData()
}