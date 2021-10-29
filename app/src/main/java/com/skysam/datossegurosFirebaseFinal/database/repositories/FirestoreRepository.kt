package com.skysam.datossegurosFirebaseFinal.database.repositories

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Account
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*


/**
 * Created by Hector Chirinos (Home) on 13/4/2021.
 */
object FirestoreRepository {
    private fun getInstance(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    fun getPasswords(): Flow<List<Password>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_CONTRASENAS)
                    .orderBy(Constants.BD_SERVICIO, Query.Direction.ASCENDING)
                    .addSnapshotListener(MetadataChanges.INCLUDE) { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val passwords: MutableList<Password> = mutableListOf()
                        for (doc in value!!) {
                            val momentoCreacion: Date = doc.getDate(Constants.BD_FECHA_CREACION)!!
                            val fechaCreacion = momentoCreacion.time
                            val calendar = Calendar.getInstance()
                            val fechaActual: Long = calendar.timeInMillis

                            val diasRestantes = fechaActual - fechaCreacion

                            val segundos = diasRestantes / 1000
                            val minutos = segundos / 60
                            val horas = minutos / 60
                            val dias = horas / 24
                            val diasTranscurridos = dias.toInt()

                            val expiration: Int = if (doc.getString(Constants.BD_VIGENCIA) == "0") {
                                0
                            } else {
                                val vencimiento: Int = doc.getString(Constants.BD_VIGENCIA)!!.toInt()
                                val faltante = vencimiento - diasTranscurridos
                                faltante
                            }

                            var labels = mutableListOf<String>()
                            if (doc.get(Constants.ETIQUETAS) != null) {
                                @Suppress("UNCHECKED_CAST")
                                labels = doc.get(Constants.ETIQUETAS) as MutableList<String>
                            }
                            val pass = Password(
                                    doc.id,
                                    doc.getString(Constants.BD_SERVICIO)!!,
                                    doc.getString(Constants.BD_USUARIO)!!,
                                    doc.getString(Constants.BD_PASSWORD)!!,
                                    expiration,
                                    fechaCreacion,
                                    labels = labels
                            )
                            passwords.add(pass)
                        }
                        offer(passwords.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getAccounts(): Flow<List<Account>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_CUENTAS)
                    .orderBy(Constants.BD_BANCO, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val accounts: MutableList<Account> = mutableListOf()
                        for (doc in value!!) {
                            val account = Account(doc.id,
                                    doc.getString(Constants.BD_TITULAR_BANCO)!!,
                                    doc.getString(Constants.BD_BANCO)!!,
                                    doc.getString(Constants.BD_NUMERO_CUENTA)!!,
                                    doc.getString(Constants.BD_CEDULA_BANCO)!!,
                                    doc.getString(Constants.BD_TIPO_DOCUMENTO)!!,
                                    doc.getString(Constants.BD_TIPO_CUENTA)!!,
                                    doc.getString(Constants.BD_TELEFONO),
                                    doc.getString(Constants.BD_CORREO_CUENTA)
                            )
                            accounts.add(account)
                        }
                        offer(accounts.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getCards(): Flow<List<Card>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_TARJETAS)
                    .orderBy(Constants.BD_TITULAR_TARJETA, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val cards: MutableList<Card> = mutableListOf()
                        for (doc in value!!) {
                            val card = Card(
                                    doc.id,
                                    doc.getString(Constants.BD_TITULAR_TARJETA)!!,
                                    doc.getString(Constants.BD_BANCO_TARJETA)!!,
                                    doc.getString(Constants.BD_NUMERO_TARJETA)!!,
                                    doc.getString(Constants.BD_CEDULA_TARJETA)!!,
                                    doc.getString(Constants.BD_TIPO_TARJETA)!!,
                                    doc.getString(Constants.BD_CVV)!!,
                                    doc.getString(Constants.BD_VENCIMIENTO_TARJETA)!!,
                                    doc.getString(Constants.BD_CLAVE_TARJETA)!!
                            )
                            cards.add(card)
                        }
                        offer(cards.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getNotes(): Flow<List<Note>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_NOTAS)
                    .orderBy(Constants.BD_TITULO_NOTAS, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val notes: MutableList<Note> = mutableListOf()
                        for (doc in value!!) {
                            val note = Note(
                                    doc.id,
                                    doc.getString(Constants.BD_TITULO_NOTAS)!!,
                                    doc.getString(Constants.BD_CONTENIDO_NOTAS)!!
                            )
                            notes.add(note)
                        }
                        offer(notes.toList())
                    }
            awaitClose { request.remove() }
        }
    }
}