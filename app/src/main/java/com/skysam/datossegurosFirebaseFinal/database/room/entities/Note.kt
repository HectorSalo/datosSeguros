package com.skysam.datossegurosFirebaseFinal.database.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skysam.datossegurosFirebaseFinal.common.Constants

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
@Entity(tableName = Constants.BD_NOTAS)
data class Note(
        @PrimaryKey val id: String,
        var title: String,
        var content: String,
        var isExpanded: Boolean = false,
        var isSavedCloud: Boolean = true
)
