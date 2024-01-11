package fr.uge.foodstock.dto

import org.json.JSONObject

data class Catalog(
    val products: List<Product>
) {
    companion object {
        fun fromJSON(json: JSONObject): Catalog {
            val products = json.getJSONArray("products")
            return Catalog(
                (0 until products.length()).map(products::getJSONObject).map { Product.fromJSON(it) }
            )
        }
    }

}
