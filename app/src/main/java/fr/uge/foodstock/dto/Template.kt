package fr.uge.foodstock.dto

import org.json.JSONObject

data class Template(
    val name: String,
    val description: String,
    val wishes: List<Wish>
) {
    companion object {
        fun fromJSON(json: JSONObject): Template {
            val consumables = json.getJSONArray("wishes")
            return Template(
                json.getString("name"),
                json.getString("description"),
                (0 until consumables.length()).map(consumables::getJSONObject).map { Wish.fromJSON(it) }
            )
        }
    }
    fun toJSONObject(): JSONObject {
        val json = JSONObject()
        json.put("name", name)
        json.put("description", description)
        json.put("wishes", wishes.map { it.toJSONObject() })
        return json
    }
}