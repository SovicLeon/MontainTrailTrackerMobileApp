package com.leonsovic.mysecondapp

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.leonsovic.lib.Hiker
import java.io.FileNotFoundException
import java.util.*

class App : Application() {
    lateinit var data: Hiker
    lateinit var id: String
    var statMap: MutableMap<String?, Int?> = LinkedHashMap()

    override fun onCreate() {
        super.onCreate()
        val preference = getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        if (preference.getString("id","").toString().isEmpty()) {
            id = UUID.randomUUID().toString().replace("-", "")
            val editor = preference.edit()
            editor.putString("id",id)
            editor.commit()
        } else {
            id = preference.getString("id","").toString()
        }

        loadStats()
        if (statMap["empty"] == 1) {
            statMap["appOpened"] = 1
            statMap["appBackground"] = 0
            statMap["mainOpened"] = 0
            statMap["aboutOpened"] = 0
            statMap["addOpened"] = 0
            statMap["addTrailOpened"] = 0
            statMap["settingsOpened"] = 0
            statMap["rViewOpened"] = 0
            statMap["empty"] = 0
            saveStats()
        } else {
            appOpened()
        }

        loadData()
    }

    fun saveStats() {
        val gson = Gson()
        val json = gson.toJson(statMap)

        val contextWrapper = ContextWrapper(applicationContext)

        val fileOutputStream = contextWrapper.openFileOutput("stats.json", Context.MODE_PRIVATE)

        fileOutputStream.write(json.toByteArray())

        fileOutputStream.close()
    }

    fun loadStats() {
        val contextWrapper = ContextWrapper(applicationContext)

        try {
            val fileInputStream = contextWrapper.openFileInput("stats.json")

            val jsonData = fileInputStream.readBytes()

            val jsonString = String(jsonData)

            val gson = Gson()
            val mapType: TypeToken<Map<String?, Int?>?> = object : TypeToken<Map<String?, Int?>?>() {}
            statMap = gson.fromJson(jsonString, mapType.type)

            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            statMap["empty"] = 1
        }
    }

    fun saveData() {
        val gson = Gson()
        val json = gson.toJson(data)

        val contextWrapper = ContextWrapper(applicationContext)

        val fileOutputStream = contextWrapper.openFileOutput("data.json", Context.MODE_PRIVATE)

        fileOutputStream.write(json.toByteArray())

        fileOutputStream.close()
    }

    fun loadData() {
        val contextWrapper = ContextWrapper(applicationContext)

        try {
            val fileInputStream = contextWrapper.openFileInput("data.json")

            val jsonData = fileInputStream.readBytes()

            val jsonString = String(jsonData)

            val gson = Gson()
            data = gson.fromJson(jsonString, Hiker::class.java)

            fileInputStream.close()
        } catch (e: FileNotFoundException) {
            data = Hiker(id)
        }
    }

    fun appOpened() {
        statMap["appOpened"] = statMap["appOpened"]?.plus(1)
        saveStats()
    }

    fun appPaused() {
        statMap["appBackground"] = statMap["appBackground"]?.plus(1)
        saveStats()
    }

    fun fragmentOpened(fragmentOpen:String) {
        statMap[fragmentOpen] = statMap[fragmentOpen]?.plus(1)
        saveStats()
    }

    fun getMeasure() : String {
        val preferencePow = getSharedPreferences("SETTINGS",Context.MODE_PRIVATE)
        return preferencePow.getString("measure","m").toString()
    }

    fun setMeasure(measure:String) {
        val sharedPreference =  getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val editor = sharedPreference.edit()
        editor.putString("measure",measure)
        editor.commit()
    }
}