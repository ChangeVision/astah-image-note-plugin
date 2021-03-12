package com.example

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object JsonSaveDataConverter {
    private val moshiForImages = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val jsonAdapter = moshiForImages.adapter(Array<String>::class.java)
    fun convertFromImagesToJSON(models: Array<String>): String = jsonAdapter.toJson(models)
    fun convertFromJsonToImages(json: String): List<String> = jsonAdapter.fromJson(json)!!.toList()
}