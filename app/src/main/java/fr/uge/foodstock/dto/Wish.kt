package fr.uge.foodstock.dto

import org.json.JSONObject

data class Wish(
    val product: Product,
    val quantity: Int
) {
    companion object {
        fun fromJSON(json: JSONObject): Wish {
            return Wish(
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

    fun toJSONObject(): JSONObject {
        val json = JSONObject()
        json.put("barCode", product.barCode)
        json.put("quantity", quantity)
        return json
    }
}