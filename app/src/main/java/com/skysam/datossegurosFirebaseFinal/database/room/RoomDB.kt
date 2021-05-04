package com.skysam.datossegurosFirebaseFinal.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Database(entities = [Password::class, Account::class, Card::class, Note::class], version = 3)
abstract class RoomDB: RoomDatabase() {
    abstract fun passwords(): com.skysam.datossegurosFirebaseFinal.database.room.daos.Password
    abstract fun accounts(): com.skysam.datossegurosFirebaseFinal.database.room.daos.Account
    abstract fun cards(): com.skysam.datossegurosFirebaseFinal.database.room.daos.Card
    abstract fun notes(): com.skysam.datossegurosFirebaseFinal.database.room.daos.Note
}