package com.skysam.datossegurosFirebaseFinal.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 27/10/2021.
 */
object LabelRepository {
    private fun getInstance(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(Constants.BD_PROPIETARIOS)
            .document(Auth.getCurrenUser()!!.uid).collection(Constants.ETIQUETAS)
    }

    fun getLabels(): Flow<MutableList<String>> {
        return callbackFlow {
            val request = getInstance().
                    orderBy(Constants.NOMBRE, Query.Direction.ASCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        Log.w(ContentValues.TAG, "Listen failed.", error)
                        return@addSnapshotListener
                    }

                    val labels = mutableListOf<String>()
                    for (doc in value!!) {
                        labels.add(doc.getString(Constants.NOMBRE)!!)
                    }
                    offer(labels)
                }
            awaitClose { request.remove() }
        }
    }

    fun addLabel(label: String) {

    }
}