package com.skysam.datossegurosFirebaseFinal.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.datossegurosFirebaseFinal.common.Constants

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Entity(tableName = Constants.BD_TARJETAS)
data class Card(
        @PrimaryKey val id: String,
        var user: String,
        var bank: String,
        var numberCard: String,
        var numberIdUser: String,
        var typeCard: String,
        var cvv: String,
        var dateExpiration: String,
        var code: String,
        var isExpanded: Boolean = false,
        var isSavedCloud: Boolean = true
)