package com.skysam.datossegurosFirebaseFinal.database.repositories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.*
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Created by Hector Chirinos on 27/10/2021.
 */
object LabelRepository {
    private fun getInstance(): DocumentReference {
        return FirebaseFirestore.getInstance()
            .collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
    }

    fun getLabels(): Flow<MutableList<String>> {
        return callbackFlow {
            val request = getInstance()
                .collection(Constants.ETIQUETAS)
                .orderBy(Constants.NOMBRE, Query.Direction.ASCENDING)
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
        val data = hashMapOf(
            Constants.NOMBRE to label
        )
        getInstance()
            .collection(Constants.ETIQUETAS)
            .add(data)
    }

    fun deleteLabels(labels: MutableList<String>) {
        for (label in labels) {
            getInstance()
                .collection(Constants.BD_CONTRASENAS)
                .whereArrayContains(Constants.ETIQUETAS, label)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        getInstance().collection(Constants.BD_CONTRASENAS)
                            .document(doc.id)
                            .update(Constants.ETIQUETAS, FieldValue.arrayRemove(label))
                    }
                }
            getInstance()
                .collection(Constants.ETIQUETAS)
                .whereEqualTo(Constants.NOMBRE, label)
                .get()
                .addOnSuccessListener {
                    for (doc in it) {
                        getInstance().collection(Constants.ETIQUETAS)
                            .document(doc.id).delete()
                    }
                }
        }
    }


}