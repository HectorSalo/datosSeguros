package com.skysam.datossegurosFirebaseFinal.database.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by Hector Chirinos (Home) on 13/4/2021.
 */
object Auth {
    fun getCurrenUser(): FirebaseUser? {
        val mAuth = FirebaseAuth.getInstance()
        return mAuth.currentUser
    }
}