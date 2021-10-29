package com.skysam.datossegurosFirebaseFinal.database.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Hector Chirinos (Home) on 28/10/2021.
 */
class RoomConverter {
    private inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    @TypeConverter
    fun toList(data: String): MutableList<String> {
        return Gson().fromJson<MutableList<String>>(data)
    }

    @TypeConverter
    fun toString(products: MutableList<String>): String {
        return Gson().toJson(products)
    }
}