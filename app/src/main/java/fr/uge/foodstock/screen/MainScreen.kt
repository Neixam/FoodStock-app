package fr.uge.foodstock.screen

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.toolbox.Volley
import fr.uge.foodstock.Routes
import fr.uge.foodstock.dto.Stock
import fr.uge.foodstock.dto.User

@Composable
fun ScreenMain(appContext: Context){
    val navController = rememberNavController()
    val queue = Volley.newRequestQueue(appContext)
    val user = remember {
        mutableStateOf<User?>(null)
    }

    LaunchedEffect(key1 = queue) {
        queue.start()
    }
    NavHost(navController = navController, startDestination = Routes.Login.route) {
        composable(Routes.Login.route) {
            LoginPage(
                navController = navController,
                queue = queue
            ) { username, password, token ->
                user.value = User(username, password, token)

            }
        }
        composable(Routes.Scanner.route) {
            Scanner(
                navController = navController,
                user = user,
                appContext = appContext,
                queue = queue
            )
        }
        composable(Routes.Register.route) {
            RegisterPage(
                navController = navController,
                queue = queue
            ) { username, password ->
                user.value = User(username, password, null)
                user.value?.update(appContext)
            }
        }
        composable(Routes.User.route) {
            UserPage(
                navController = navController
            )
        }
        composable(Routes.Stock.route) {
            StockPage(
                user = user,
                queue = queue
            )
        }
        composable(Routes.Template.route) {
            TemplatePage(
                navController = navController,
                user = user,
                queue = queue
            )
        }
        composable(Routes.CatalogSearch.route) {
            CatalogSearch(
                navController = navController,
                user = user,
                queue = queue
            )
        }
    }
}