package com.skysam.datossegurosFirebaseFinal.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.datossegurosFirebaseFinal.common.Constants

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Entity(tableName = Constants.BD_CUENTAS)
data class Account(
        @PrimaryKey(autoGenerate = true) val id: Int,
        var user: String,
        var bank: String,
        var numberAccount: String,
        var numberIdUser: String,
        var typeIdUSer: String,
        var typeAccount: String,
        var telph: String,
        var email: String,
)

