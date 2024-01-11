package fr.uge.foodstock.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import fr.uge.foodstock.Routes
import fr.uge.foodstock.dto.Product
import org.json.JSONObject
import fr.uge.foodstock.ScannerBarCode.Companion.apiUrl
import fr.uge.foodstock.dto.User

fun subProduct(queue: RequestQueue, product: Product, token: String, onSuccessCallback: () -> Unit = {}) {
    val urlToCall = apiUrl + "stock/consumable/sub/" + product.barCode

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.POST, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback()
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

fun addProduct(queue: RequestQueue, product: Product, token: String, onSuccessCallback: () -> Unit = {}) {
    val urlToCall = apiUrl + "stock/consumable/add/" + product.barCode

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.POST, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback()
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

private fun getProduct(queue: RequestQueue, token: String, barcode: String, onSuccessCallback: (Product) -> Unit, onErrorCallBack: (Int) -> Unit) {
    val urlToCall = (apiUrl + "product/{barcode}").replace("{barcode}", barcode)

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.GET, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback(
                Product.fromJSON(response)
            )
        },
        Response.ErrorListener { error ->
            Log.e("CallApi", "Error $error")
            error?.let {
                it.networkResponse?.let {
                    onErrorCallBack(it.statusCode)
                }
            }
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

private fun scanner(
    appContext: Context,
    onBarcodeScanned: (String) -> Unit
) {
    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()
    val scanner = GmsBarcodeScanning.getClient(appContext, options)
    scanner.startScan()
        .addOnSuccessListener { barcode ->
            // Task completed successfully
            barcode.rawValue?.let { onBarcodeScanned(it) }
        }
        .addOnCanceledListener {
            // Task canceled
        }
        .addOnFailureListener { e ->
            // Task failed with an exception
        }
}

@Composable
fun Scanner(
    navController: NavHostController,
    appContext: Context,
    queue: RequestQueue,
    user: MutableState<User?>,
    modifier: Modifier = Modifier
) {
    var product by remember {
        mutableStateOf<Product?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        Card (
            Modifier
                .fillMaxWidth()
                .weight(0.7F, fill = true)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Log.i("Scanner", "actualProduct : $product")
                product?.BlockProduct(onAdd = {
                    addProduct(queue, it, user.value!!.token!!)
                }, onRemove = {
                    subProduct(queue, it, user.value!!.token!!)
                })
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ){
            Spacer(modifier = Modifier.width(10.dp))
            user.value?.BlockUser(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable {
                        navController.navigate(Routes.User.route)
                    }
            )
            Button(
                modifier = Modifier
                    .align(Alignment.Center),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                onClick = {
                    scanner(appContext) {
                        user.value?.token?.let { token ->
                            getProduct(queue, token, it, { newProduct ->
                                product = newProduct
                            }, { statusCode ->
                                if (statusCode == 403) user.value?.update(appContext)
                            })
                        }
                    }
                }) {
                Text(
                    text = "Scan",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.inversePrimary,
                )
            }
        }
    }
}