package com.skysam.datossegurosFirebaseFinal.database.room.daos

import androidx.room.*
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import kotlinx.coroutines.flow.Flow

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Dao
interface Card {
    @Query("SELECT * FROM tarjetas ORDER BY user ASC")
    fun getAll(): Flow<MutableList<Card>>

    @Query("SELECT * FROM tarjetas WHERE id = :id")
    suspend fun getCardById(id: String): Card

    @Insert
    suspend fun insert(card: Card)

    @Update
    suspend fun update(card: Card)

    @Delete
    suspend fun delete(card: Card)

    @Query("DELETE FROM tarjetas")
    suspend fun deleteAll()
}