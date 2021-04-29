package com.skysam.datossegurosFirebaseFinal.database.room

import androidx.room.Room
import com.skysam.datossegurosFirebaseFinal.common.DatosSeguros
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
object Room: CoroutineScope {
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private const val NAME_DB_ROOM = "dataSafe"

    private fun getInstance(): RoomDB {
        return Room.databaseBuilder(DatosSeguros.DatosSeguros.getContext(),
        RoomDB::class.java, NAME_DB_ROOM)
                .fallbackToDestructiveMigration()
                .build()
    }

    fun savePassword(password: Password) {
        launch {
            getInstance().passwords()
                    .delete(password)
        }
    }

    fun updatePassword(password: Password) {
        launch {
            getInstance().passwords()
                    .update(password)
        }
    }

    fun deletePassword(password: Password) {
        launch {
            getInstance().passwords()
                    .delete(password)
        }
    }

    fun deleteAllPasswords() {
        launch {
            getInstance().passwords()
                    .deleteAll()
        }
    }

    fun saveAccount(account: Account) {
        launch {
            getInstance().accounts()
                    .delete(account)
        }
    }

    fun updateAccount(account: Account) {
        launch {
            getInstance().accounts()
                    .update(account)
        }
    }

    fun deleteAccount(account: Account) {
        launch {
            getInstance().accounts()
                    .delete(account)
        }
    }

    fun deleteAllAccounts() {
        launch {
            getInstance().accounts()
                    .deleteAll()
        }
    }

    fun saveCard(card: Card) {
        launch {
            getInstance().cards()
                    .delete(card)
        }
    }

    fun updateCard(card: Card) {
        launch {
            getInstance().cards()
                    .update(card)
        }
    }

    fun deleteCard(card: Card) {
        launch {
            getInstance().cards()
                    .delete(card)
        }
    }

    fun deleteAllCards() {
        launch {
            getInstance().cards()
                    .deleteAll()
        }
    }

    fun saveNote(note: Note) {
        launch {
            getInstance().notes()
                    .delete(note)
        }
    }

    fun updateNote(note: Note) {
        launch {
            getInstance().notes()
                    .update(note)
        }
    }

    fun deleteNote(note: Note) {
        launch {
            getInstance().notes()
                    .delete(note)
        }
    }

    fun deleteAllNotes() {
        launch {
            getInstance().notes()
                    .deleteAll()
        }
    }
}