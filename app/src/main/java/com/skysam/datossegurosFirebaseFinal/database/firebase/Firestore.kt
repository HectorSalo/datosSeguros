package com.skysam.datossegurosFirebaseFinal.database.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*


/**
 * Created by Hector Chirinos (Home) on 13/4/2021.
 */
object Firestore {
    private fun getInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @ExperimentalCoroutinesApi
    fun getPasswords(): Flow<List<PasswordsModel>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_CONTRASENAS)
                    .orderBy(Constants.BD_SERVICIO, Query.Direction.ASCENDING)
                    .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val map = value?.documents?.mapNotNull { it.toObject(PasswordsModel::class.java) }

                        offer(map!!)

                        /*for (dc in value!!.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> {
                                    val pass = PasswordsModel()
                                    pass.idContrasena = dc.document.id
                                    pass.servicio = dc.document.getString(Constants.BD_SERVICIO)
                                    pass.usuario = dc.document.getString(Constants.BD_USUARIO)
                                    pass.contrasena = dc.document.getString(Constants.BD_PASSWORD)

                                    val momentoCreacion: Date = dc.document.getDate(Constants.BD_FECHA_CREACION)!!
                                    val fechaCreacion = momentoCreacion.time
                                    val calendar = Calendar.getInstance()
                                    val fechaActual: Long = calendar.timeInMillis

                                    val diasRestantes = fechaActual - fechaCreacion

                                    val segundos = diasRestantes / 1000
                                    val minutos = segundos / 60
                                    val horas = minutos / 60
                                    val dias = horas / 24
                                    val diasTranscurridos = dias.toInt()


                                    if (dc.document.getString(Constants.BD_VIGENCIA) == "0") {
                                        pass.vencimiento = 0
                                    } else {
                                        val vencimiento: Int = dc.document.getString(Constants.BD_VIGENCIA)!!.toInt()
                                        val faltante = vencimiento - diasTranscurridos
                                        pass.vencimiento = faltante
                                    }
                                }
                                DocumentChange.Type.MODIFIED -> {

                                }
                                DocumentChange.Type.REMOVED -> {

                                }
                            }
                        }*/
                    }
            awaitClose { request.remove() }
        }
    }
}