package com.manele.spesify.application

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.manele.spesify.presentation.ShoppingListDetailScreen
import com.manele.spesify.presentation.ShoppingListsScreen
import com.manele.spesify.repo.ShoppingListRepository

sealed class Screen(val route: String) {
    object ShoppingLists : Screen("shopping_lists")
    object ShoppingListDetail : Screen("shopping_list_detail/{listId}") {
        fun createRoute(listId: Long) = "shopping_list_detail/$listId"
    }
}

@Composable
fun AppNavigation(repository: ShoppingListRepository) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ShoppingLists.route) {
        composable(Screen.ShoppingLists.route) {
            ShoppingListsScreen(navController, repository)
        }
        composable(Screen.ShoppingListDetail.route) { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")?.toLongOrNull() ?: 0L
            ShoppingListDetailScreen(navController, listId, repository)
        }
    }
}
