package com.leonsovic.lib

import java.util.*

class Trail (private val title: String, private val distance: Double, private val altitude: Double, private val lat: String, private val lon: String) {
    var id:String = UUID.randomUUID().toString().replace("-", "")

    fun getTitle(): String {
        return title
    }

    fun getDistance(): Double {
        return distance
    }

    fun getAltitude(): Double {
        return altitude
    }

    fun getLon(): String {
        return lon
    }

    fun getLat(): String {
        return lat
    }

    override fun toString(): String {
        return "Trail ID: $id, distance: $distance, altitude: $altitude, title: $title"
    }
}