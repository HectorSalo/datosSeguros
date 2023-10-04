package com.skysam.datossegurosFirebaseFinal.generalActivitys

import androidx.lifecycle.*
import com.skysam.datossegurosFirebaseFinal.database.repositories.FirestoreRepository
import com.skysam.datossegurosFirebaseFinal.database.repositories.LabelRepository
import com.skysam.datossegurosFirebaseFinal.common.model.Account
import com.skysam.datossegurosFirebaseFinal.common.model.Card
import com.skysam.datossegurosFirebaseFinal.common.model.Note
import com.skysam.datossegurosFirebaseFinal.common.model.Password

class MainViewModel : ViewModel() {
    val passwordsFirestore: LiveData<List<Password>> = FirestoreRepository.getPasswords().asLiveData()
    val accountsFirestore: LiveData<List<Account>> = FirestoreRepository.getAccounts().asLiveData()
    val cardsFirestore: LiveData<List<Card>> = FirestoreRepository.getCards().asLiveData()
    val notesFirestore: LiveData<List<Note>> = FirestoreRepository.getNotes().asLiveData()
    val labels: LiveData<MutableList<String>> = LabelRepository.getLabels().asLiveData()

    fun deleteLabels(labels: MutableList<String>) {
        LabelRepository.deleteLabels(labels)
    }
}