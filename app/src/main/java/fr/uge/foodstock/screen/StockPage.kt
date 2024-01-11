package fr.uge.foodstock.screen

import android.util.Log
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import fr.uge.foodstock.Routes
import fr.uge.foodstock.ScannerBarCode.Companion.apiUrl
import fr.uge.foodstock.dto.Stock
import fr.uge.foodstock.dto.User
import org.json.JSONObject

private fun getStock(queue: RequestQueue, token: String, onSuccessCallback: (Stock) -> Unit) {
    val urlToCall = apiUrl + "stock"

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.GET, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback(Stock.fromJSON(response))
        },
        Response.ErrorListener { error ->
            Log.e("CallApi", "Error $error")
        }
    ) {
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>(super.getHeaders())
            headers["Authorization"] = "Bearer $token"
            return headers
        }
    }

    queue.add(jsonObjectRequest)
}

@Composable
fun StockPage(
    user: MutableState<User?>,
    queue: RequestQueue
) {
    val stock = remember {
        mutableStateOf<Stock?>(null)
    }
    stock.value?.let {
        Box(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                it.consumables.forEach { consumable ->
                    consumable.product.BlockProduct(
                        onAdd = {
                            addProduct(queue, it, user.value!!.token!!) {
                                getStock(queue, user.value!!.token!!) {
                                    stock.value = it
                                }
                            }

                        },
                        onRemove = {
                            subProduct(queue, it, user.value!!.token!!) {
                                getStock(queue, user.value!!.token!!) {
                                    stock.value = it
                                }
                            }
                        },
                        number = consumable.quantity
                    )
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp))
                }
            }
        }
    } ?: run {
        getStock(queue, user.value!!.token!!) {
            it.consumables.forEach { consumable ->
                Log.i("StockPage", "Consumable ${consumable.product.label}")
            }
            stock.value = it
        }
    }
}