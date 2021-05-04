package com.skysam.datossegurosFirebaseFinal.database.room.daos

import androidx.room.*
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Dao
interface Note {
    @Query("SELECT * FROM notas ORDER BY title ASC")
    fun getAll(): Flow<MutableList<Note>>

    @Query("SELECT * FROM notas WHERE id = :id")
    suspend fun getNoteById(id: String): Note

    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM notas")
    suspend fun deleteAll()
}