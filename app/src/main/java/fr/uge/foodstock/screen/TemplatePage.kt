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
import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import fr.uge.foodstock.Routes
import fr.uge.foodstock.ScannerBarCode
import fr.uge.foodstock.dto.Stock
import fr.uge.foodstock.dto.Template
import fr.uge.foodstock.dto.User
import org.json.JSONObject

private fun getTemplate(queue: RequestQueue, token: String, onSuccessCallback: (Template) -> Unit) {
    val urlToCall = ScannerBarCode.apiUrl + "template"

    val jsonObjectRequest = object : JsonObjectRequest(
        Method.GET, urlToCall, JSONObject(),
        Response.Listener { response ->
            Log.i("CallApi", "jsonResp $response")
            onSuccessCallback(Template.fromJSON(response))
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
fun TemplatePage(
    navController: NavController,
    user: MutableState<User?>,
    queue: RequestQueue
){
    val template = remember {
        mutableStateOf<Template?>(null)
    }
    template.value?.let {
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
                        it.wishes.forEach { consumable ->
                            consumable.product.BlockProduct(
                                onAdd = {
                                    addProduct(queue, it, user.value!!.token!!) {
                                        getTemplate(queue, user.value!!.token!!) {
                                            template.value = it
                                        }
                                    }

                                },
                                onRemove = {
                                    subProduct(queue, it, user.value!!.token!!) {
                                        getTemplate(queue, user.value!!.token!!) {
                                            template.value = it
                                        }
                                    }
                                },
                                number = consumable.quantity
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
                            navController.navigate(Routes.CatalogSearch.route)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Adding new product")
                    }
                }
            }
        }
    } ?: run {
        getTemplate(queue, user.value!!.token!!) {
            it.wishes.forEach { wish ->
                Log.i("TemplatePage", "Consumable ${wish.product.label}")
            }
            template.value = it
        }
    }
}