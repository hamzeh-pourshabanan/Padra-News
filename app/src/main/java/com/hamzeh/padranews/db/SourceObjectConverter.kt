package com.hamzeh.padranews.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hamzeh.padranews.model.Source

class SourceObjectConverter {
    @TypeConverter
    fun toSourceJson(source: Source): String {
        val type = object :TypeToken<Source>() {}.type
        return Gson().toJson(source, type)
    }

    @TypeConverter
    fun toSource(sourceJson: String): Source {
        val type = object :TypeToken<Source>(){}.type
        return Gson().fromJson(sourceJson, type)
    }
}