package com.skysam.datossegurosFirebaseFinal.common.model

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */

data class Note(
        val id: String,
        var title: String,
        var content: String,
        var isExpanded: Boolean = false,
        var isSavedCloud: Boolean = true,
        var labels: MutableList<String>
)
