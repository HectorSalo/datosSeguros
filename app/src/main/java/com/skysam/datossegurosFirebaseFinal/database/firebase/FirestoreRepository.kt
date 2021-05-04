package com.skysam.datossegurosFirebaseFinal.database.firebase

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel
import com.skysam.datossegurosFirebaseFinal.common.model.CardModel
import com.skysam.datossegurosFirebaseFinal.common.model.NoteModel
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

                            val pass = Password(
                                    doc.id,
                                    doc.getString(Constants.BD_SERVICIO)!!,
                                    doc.getString(Constants.BD_USUARIO)!!,
                                    doc.getString(Constants.BD_PASSWORD)!!,
                                    expiration,
                                    fechaCreacion
                            )
                            passwords.add(pass)
                        }
                        offer(passwords.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getAccounts(): Flow<List<AccountModel>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_CUENTAS)
                    .orderBy(Constants.BD_BANCO, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val accounts: MutableList<AccountModel> = mutableListOf()
                        for (doc in value!!) {
                            val account = AccountModel()
                            account.idCuenta = doc.id
                            account.titular = doc.getString(Constants.BD_TITULAR_BANCO)
                            account.banco = doc.getString(Constants.BD_BANCO)
                            account.numeroCuenta = doc.getString(Constants.BD_NUMERO_CUENTA)
                            account.cedula = doc.getString(Constants.BD_CEDULA_BANCO)
                            account.tipo = doc.getString(Constants.BD_TIPO_CUENTA)
                            account.telefono = doc.getString(Constants.BD_TELEFONO)
                            account.correo = doc.getString(Constants.BD_CORREO_CUENTA)
                            account.tipoDocumento = doc.getString(Constants.BD_TIPO_DOCUMENTO)

                            accounts.add(account)
                        }
                        offer(accounts.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getCards(): Flow<List<CardModel>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_TARJETAS)
                    .orderBy(Constants.BD_TITULAR_TARJETA, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val cards: MutableList<CardModel> = mutableListOf()
                        for (doc in value!!) {
                            val card = CardModel()
                            card.idTarjeta = doc.id
                            card.titular = doc.getString(Constants.BD_TITULAR_TARJETA)
                            card.numeroTarjeta = doc.getString(Constants.BD_NUMERO_TARJETA)
                            card.cvv = doc.getString(Constants.BD_CVV)
                            card.cedula = doc.getString(Constants.BD_CEDULA_TARJETA)
                            card.tipo = doc.getString(Constants.BD_TIPO_TARJETA)
                            card.banco = doc.getString(Constants.BD_BANCO_TARJETA)
                            card.vencimiento = doc.getString(Constants.BD_VENCIMIENTO_TARJETA)
                            card.clave = doc.getString(Constants.BD_CLAVE_TARJETA)

                            cards.add(card)
                        }
                        offer(cards.toList())
                    }
            awaitClose { request.remove() }
        }
    }

    fun getNotes(): Flow<List<NoteModel>> {
        return callbackFlow {
            val request = getInstance().collection(Constants.BD_PROPIETARIOS).document(Auth.getCurrenUser()!!.uid)
                    .collection(Constants.BD_NOTAS)
                    .orderBy(Constants.BD_TITULO_NOTAS, Query.Direction.ASCENDING)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error)
                            return@addSnapshotListener
                        }

                        val notes: MutableList<NoteModel> = mutableListOf()
                        for (doc in value!!) {
                            val note = NoteModel()
                            note.idNota = doc.id
                            note.titulo = doc.getString(Constants.BD_TITULO_NOTAS)
                            note.contenido = doc.getString(Constants.BD_CONTENIDO_NOTAS)

                            notes.add(note)
                        }
                        offer(notes.toList())
                    }
            awaitClose { request.remove() }
        }
    }
}