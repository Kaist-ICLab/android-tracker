package dev.iclab.tracker

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object Util {
    const val TAG = "Util"
    fun map2Json(map: Map<String, Any>, prettify: Boolean = true):String {
        val gson: Gson = if (prettify) {
            GsonBuilder().setPrettyPrinting().create()
        } else {
            GsonBuilder().create()
        }
        return gson.toJson(map)
    }
}