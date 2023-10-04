package com.skysam.datossegurosFirebaseFinal.common.model

/**
 * Created by Hector Chirinos (Home) on 28/4/2021.
 */
data class Card(
        val id: String,
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