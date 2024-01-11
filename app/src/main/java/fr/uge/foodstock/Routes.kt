package fr.uge.foodstock

sealed class Routes(
    val route: String
) {
    object Login : Routes("Login")
    object Register : Routes("Register")
    object Scanner : Routes("Scanner")
    object User : Routes("User")
    object Template : Routes("Template")
    object Stock : Routes("Stock")
    object CatalogSearch : Routes("CatalogSearch")
}