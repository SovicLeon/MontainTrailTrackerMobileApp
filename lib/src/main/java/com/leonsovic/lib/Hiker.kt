package com.leonsovic.lib

class Hiker (private val ID: String) {
    var hikeList:MutableList<Hike> = mutableListOf()
    var trailList:MutableList<Trail> = mutableListOf()

    fun addHike(newHike: Hike) {
        hikeList.add(newHike)
    }

    fun addTrail(newTrail: Trail) {
        trailList.add(newTrail)
    }

    fun removeHike(hikeIndex: Int) {
        hikeList.removeAt(hikeIndex)
    }

    fun hikeListToString(): String {
        var hikeString = ""
        for (hike in hikeList) {
            hikeString += "\n" + hike.toString()
        }
        return hikeString
    }

    private fun size(): Int {
        return hikeList.size
    }

    override fun toString(): String {
        val hikeString:String = hikeListToString()
        return "ID: $ID, Number of hikes: ${size()}\nHikes:$hikeString"
    }
}