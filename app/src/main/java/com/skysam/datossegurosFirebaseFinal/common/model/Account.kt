package com.skysam.datossegurosFirebaseFinal.common.model

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
data class Account(
        val id: String,
        var user: String,
        var bank: String,
        var numberAccount: String,
        var numberIdUser: String,
        var typeIdUSer: String,
        var typeAccount: String,
        var telph: String? = null,
        var email: String? = null,
        var isExpanded: Boolean = false,
        var isSavedCloud: Boolean = true
)

