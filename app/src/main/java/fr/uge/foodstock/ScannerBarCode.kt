package fr.uge.foodstock

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fr.uge.foodstock.ui.theme.FoodStockTheme
import android.Manifest.permission.CAMERA;
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import fr.uge.foodstock.dto.ProductResponse
import kotlin.coroutines.coroutineContext

class ScannerBarCode : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
        } else {
            Log.i("kilo", "Permission denied")
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(CAMERA)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodStockTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scanner(appContext = this)
                }
            }
        }
        requestCameraPermission()
    }
}
private val apiURL = "https://fr.openfoodfacts.org/api/v2/produit/{barcode}.json"
fun callApi(queue: RequestQueue, barcode: String, callback: (ProductResponse) -> Unit) {
    val urlToCall = apiURL.replace("{barcode}", barcode)

    val jsonObjectRequest = JsonObjectRequest(
        Request.Method.GET, urlToCall, null,
        { response ->
            val product = response.getJSONObject("product")
            callback(ProductResponse(product["image_front_url"].toString(), product["product_name"].toString()))
        },
        { error ->
            // TODO: Handle error
        }
    )
    queue.add(jsonObjectRequest)
}

fun scanner(
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
fun Scanner(appContext: Context, modifier: Modifier = Modifier) {
    var product by remember {
        mutableStateOf<ProductResponse?>(null)
    }
    val queue = Volley.newRequestQueue(appContext)

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {

        Card (
            Modifier
                .fillMaxWidth()
                .weight(0.7F, fill = true)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                AsyncImage(
                    model = product?.image,
                    contentDescription = "Product Front",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            onClick = {
                scanner(appContext) {
                    callApi(queue, it) { newProduct ->
                        product = newProduct
                    }
                    queue.start()
                }
            }) {
            Text(
                text = "Scan Barcode",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.inversePrimary,
            )
        }
    }
}

@Preview
@Composable
fun Test() {
    Scanner(appContext = LocalContext.current)
}
