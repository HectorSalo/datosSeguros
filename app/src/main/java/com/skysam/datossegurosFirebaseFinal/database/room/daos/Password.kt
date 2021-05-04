package com.skysam.datossegurosFirebaseFinal.database.room.daos

import androidx.room.*
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Dao
interface Password {
    @Query("SELECT * FROM contrasenas ORDER BY service ASC")
    fun getAll(): Flow<MutableList<Password>>

    @Query("SELECT * FROM contrasenas WHERE id = :id")
    suspend fun getPasswordById(id: String): Password

    @Insert
    suspend fun insert(password: Password)

    @Update
    suspend fun update(password: Password)

    @Delete
    suspend fun delete(password: Password)

    @Query ("DELETE FROM contrasenas")
    suspend fun deleteAll()
}