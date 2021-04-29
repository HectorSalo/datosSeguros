package com.skysam.datossegurosFirebaseFinal.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.datossegurosFirebaseFinal.common.Constants

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Entity(tableName = Constants.BD_CONTRASENAS)
data class Password(
        @PrimaryKey(autoGenerate = true) val id: Int,
        var service: String,
        var user: String,
        var password: String,
        var duration: Int,
        var dateCreated: Long,
        var passOld1: String,
        var passOld2: String,
        var passOld3: String,
        var passOld4: String,
        var passOld5: String
)
