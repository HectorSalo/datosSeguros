package com.skysam.datossegurosFirebaseFinal.common.model

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */

data class Password(
        val id: String,
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
        var labels: MutableList<String>
)
