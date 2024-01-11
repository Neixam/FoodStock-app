package fr.uge.foodstock.dto

import org.json.JSONObject

data class Stock(
    val consumables: List<Consumable>
) {
    companion object {
        fun fromJSON(json: JSONObject): Stock {
            val consumables = json.getJSONArray("consumables")
            return Stock(
                (0 until consumables.length()).map(consumables::getJSONObject).map { Consumable.fromJSON(it) }
            )
        }
    }
}