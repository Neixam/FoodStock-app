package fr.uge.foodstock.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import fr.uge.foodstock.Routes
import fr.uge.foodstock.ScannerBarCode
import org.json.JSONObject

private fun registerApi(queue: RequestQueue, params: Pair<String, String>, onSuccessCallback: () -> Unit, onErrorCallback: (String) -> Unit) {
    val urlToCall = ScannerBarCode.apiUrl + "register"
    val request = object : StringRequest(
        Method.POST, urlToCall, { response ->
            onSuccessCallback()
        }, { error ->
            if (error.networkResponse.statusCode == 403) {
                onErrorCallback("Register failed")
            } else {
                onErrorCallback("Server error try later")
            }
        }
    ) {
        override fun getBody(): ByteArray {
            val param = HashMap<String, String>()
            param["username"] = params.first
            param["password"] = params.second
            return JSONObject(param.toMap()).toString().encodeToByteArray()
        }

        override fun getBodyContentType(): String {
            return "application/json"
        }
    }
    queue.add(request)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterPage(
    navController: NavHostController,
    queue: RequestQueue,
    onRegisterSuccess: (String, String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ClickableText(
            text = AnnotatedString("Sign in here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {
                      navController.navigate(Routes.Login.route)
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = Color.Cyan
            )
        )
    }
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val username = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }
        val error = remember {
            mutableStateOf<String?>(null)
        }

        Text(text = "Register", style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Cursive))

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    val username = username.component1().text
                    val password = password.component1().text
                    registerApi(
                        queue,
                        Pair(username, password),
                        {
                            navController.navigate(Routes.Login.route)
                            onRegisterSuccess(username, password)
                        }, {
                            error.value = it
                        }
                    )
                    //queue.start()
                },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Register")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        error.value?.let {text ->
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = text,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}