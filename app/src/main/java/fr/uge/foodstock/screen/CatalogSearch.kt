package fr.uge.foodstock.screen

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.navigation.NavController
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import fr.uge.foodstock.Routes
import fr.uge.foodstock.ScannerBarCode
import fr.uge.foodstock.dto.Catalog
import fr.uge.foodstock.dto.Product
import fr.uge.foodstock.dto.Stock
import fr.uge.foodstock.dto.Template
import fr.uge.foodstock.dto.User
import fr.uge.foodstock.dto.Wish
import org.json.JSONObject

private fun addAllProduct(template: Template, queue: RequestQueue, token: String, onSuccessCallback: () -> Unit = {}) {
    val urlToCall = ScannerBarCode.apiUrl + "template/wishes/add"

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.PATCH, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback()
        },
        Response.ErrorListener { error ->
            Log.e("CallApi", "Error $error")
        }
    ) {
        override fun getBody(): ByteArray {
            Log.i("CallApi", "token ${template.toJSONObject()}")

            return template.toJSONObject().toString().toByteArray()
        }

        override fun getBodyContentType(): String {
            return "application/json"
        }
        override fun getHeaders(): Map<String, String> {
            val headers = HashMap<String, String>(super.getHeaders())
            headers["Authorization"] = "Bearer $token"
            headers["X-HTTP-Method-Override"] = "PATCH"
            return headers
        }
    }

    queue.add(jsonObjectRequest)
}

private fun getCatalog(queue: RequestQueue, token: String, onSuccessCallback: (Catalog) -> Unit) {
    val urlToCall = ScannerBarCode.apiUrl + "catalog"

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.GET, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback(Catalog.fromJSON(response))
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
fun CatalogSearch(
    navController: NavController,
    user: MutableState<User?>,
    queue: RequestQueue
) {
    val catalog = remember {
        mutableStateOf<Catalog?>(null)
    }
    val template = remember {
        mutableStateOf<Map<Product, Int>>(emptyMap())
    }
    catalog.value?.let {
        Box(
            Modifier
                .fillMaxSize()
        ) {
            Column {
                Row(
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(0.9F, fill = true)
                ) {
                    Column {
                        it.products.forEach { product ->
                            product.BlockProduct(
                                onAdd = { addingProduct ->
                                    template.value = template.value.filter { it.key == addingProduct } + mapOf(product to (template.value.getOrDefault(product, 0) + 1))
                                },
                                onRemove = { subingProduct ->
                                    template.value = template.value.filter { it.key == subingProduct } + mapOf(product to (template.value.getOrDefault(product, 0) - 1))
                                },
                                number = template.value.getOrDefault(product, 0)
                            )
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp))
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(0.1F, fill = true)
                ) {
                    Button(
                        onClick = {
                            addAllProduct(Template(
                                "",
                                "",
                                template.value.filter { it.value > 0 }.map { Wish(it.key, it.value) }
                            ), queue, user.value!!.token!!) {
                                navController.navigate(Routes.Stock.route)
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Add all")
                    }
                }
            }
        }
    } ?: run {
        getCatalog(queue, user.value!!.token!!) {
            Log.i("CatalogSearch", "Catalog ${it.products.size}")
            catalog.value = it
        }
    }
}