package fr.uge.foodstock.dto

import org.json.JSONObject

data class Consumable(
    val product: Product,
    val quantity: Int
) {
    companion object {
        fun fromJSON(json: JSONObject): Consumable {
            return Consumable(
                Product(
                    json.getString("image"),
                    json.getString("name"),
                    json.getString("weight"),
                    json.getString("barCode")
                ),
                json.getInt("quantity")
            )
        }
    }
}