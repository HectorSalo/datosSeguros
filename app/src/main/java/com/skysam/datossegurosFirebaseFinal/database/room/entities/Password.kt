package com.skysam.datossegurosFirebaseFinal.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.database.room.RoomConverter

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Entity(tableName = Constants.BD_CONTRASENAS)
data class Password(
        @PrimaryKey val id: String,
        var service: String,
        var user: String,
        var password: String,
        var expiration: Int,
        var dateCreated: Long,
        var isExpanded: Boolean = false,
        var passOld1: String? = null,
        var passOld2: String? = null,
        var passOld3: String? = null,
        var passOld4: String? = null,
        var passOld5: String? = null,
        var isSavedCloud: Boolean = true,
        @TypeConverters(RoomConverter::class)
        var labels: MutableList<String>
)
