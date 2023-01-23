package com.leonsovic.lib

import java.util.*

class Hike (private val title: String, private val description: String, private val trail: Trail, private val date: Date) {
    var id:String = UUID.randomUUID().toString().replace("-", "")

    fun getTrail(): Trail {
        return trail
    }

    fun getDate(): Date {
        return date
    }

    fun getTitle(): String {
        return title
    }

    fun getDescription(): String {
        return description
    }

    override fun toString(): String {
        return "Hike Title:$title\nDescription: $description\nID: $id\nTrail: $trail"
    }
}