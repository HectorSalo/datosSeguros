package com.skysam.datossegurosFirebaseFinal.database.room.daos

import androidx.room.*
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Dao
interface Account {
    @Query("SELECT * FROM cuentas ORDER BY bank ASC")
    fun getAll(): Flow<MutableList<Account>>

    @Insert
    suspend fun insert(account: Account)

    @Update
    suspend fun update(account: Account)

    @Delete
    suspend fun delete(account: Account)

    @Query("DELETE FROM cuentas")
    suspend fun deleteAll()
}