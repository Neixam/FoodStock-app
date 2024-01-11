package fr.uge.foodstock.dto

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import fr.uge.foodstock.ScannerBarCode
import org.json.JSONObject

data class User(
    val username: String,
    val password: String,
    var token: String?,
) {
    @Composable
    fun BlockUser(modifier: Modifier = Modifier) {
        Log.i("BlockUser", "Actual Context $username and $password")
        Box(
            modifier = modifier
                .background(Color.LightGray, RoundedCornerShape(10.dp))
                .width(50.dp)
                .height(50.dp)
        ) {
            Text(
                text = username,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier
                    .background(Color.LightGray, CircleShape)
                    .width(50.dp)
                    .height(50.dp)
            )
        }
    }

    fun update(appContext: Context) {
        val url = ScannerBarCode.apiUrl + "login"
        val queue = Volley.newRequestQueue(appContext);
        val request = object : StringRequest(
            Method.POST, url, { response ->
                token = response
            }, { error ->
                Log.e("UserUpdate", "$error")
            }
        ) {
            override fun getBody(): ByteArray {
                val param = HashMap<String, String>()
                param["username"] = username
                param["password"] = password
                return JSONObject(param.toMap()).toString().encodeToByteArray()
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        queue.add(request)
        queue.start()
    }
}