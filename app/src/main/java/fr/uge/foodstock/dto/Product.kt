package fr.uge.foodstock.dto

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import org.json.JSONObject


data class Product(
    val image: String,
    val label: String,
    val quantity: String,
    val barCode: String
) {
    @Composable
    fun BlockProduct(
        onAdd: (Product) -> Unit = {},
        onRemove: (Product) -> Unit = {},
        number: Int? = null
    ) {
        Log.i("BlockProduct", "Actual Context $image and $label")
        Box (
            Modifier
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            ) {
                Row(
                   modifier = Modifier
                       .fillMaxWidth()
                       .height(100.dp)
                       .background(Color(0xFFFFFFFF), RoundedCornerShape(10.dp))
                       .border(1.dp, Color(0x80FFFFFF), RoundedCornerShape(10.dp))
                ){
                    Spacer(modifier = Modifier.width(10.dp))
                    AsyncImage(
                        model = image,
                        contentDescription = label,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Name: $label",
                            modifier = Modifier,
                            color = Color.Black
                        )
                        Text(
                            text = "Quantity: $quantity",
                            modifier = Modifier,
                            color = Color.Black
                        )
                        Text(
                            text = "BarCode: $barCode",
                            modifier = Modifier,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(30.dp))

                    number?.let {
                        Column {
                            Text(
                                text = "x$it",
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.Black
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    Button(
                        onClick = {
                            onAdd(this@Product)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(0.5F, fill = true)
                            .border(1.dp, Color(0x80008000), RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            // Dark Green
                            containerColor = Color(0xFF008000)
                        )
                    ) {
                        Text(text = "Add", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            onRemove(this@Product)
                        },
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .height(50.dp)
                            .weight(0.5F, fill = true)
                            .border(1.dp, Color(0x80800000), RoundedCornerShape(50.dp)),
                        colors = ButtonDefaults.buttonColors(
                            // Dark Red
                            containerColor = Color(0xFF800000)
                        )
                    ) {
                        Text(text = "Remove", color = Color.White)
                    }
                }
            }
        }
    }
    companion object {
        fun fromJSON(json: JSONObject): Product {
            return Product(
                json.getString("image"),
                json.getString("name"),
                json.getString("weight"),
                json.getString("barCode")
            )
        }
    }

    fun toJSONObject(): JSONObject {
        val json = JSONObject()
        json.put("image", image)
        json.put("name", label)
        json.put("weight", quantity)
        json.put("barCode", barCode)
        return json
    }

    override fun toString(): String {
        return "Product{url:$image, name:$label}"
    }
}