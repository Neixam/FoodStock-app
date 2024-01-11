package fr.uge.foodstock.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.android.volley.RequestQueue
import fr.uge.foodstock.Routes
import fr.uge.foodstock.dto.User

@Composable
fun UserPage(
    navController: NavController
) {
    Box(modifier = Modifier) {
        Column {
            Button(
                onClick = {
                    navController.navigate(Routes.Stock.route)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(text = "Stock")
            }
            Button(
                onClick = {
                    navController.navigate(Routes.Template.route)
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(text = "Template")
            }
        }
    }
}