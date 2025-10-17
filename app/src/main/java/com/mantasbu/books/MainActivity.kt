package com.mantasbu.books

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mantasbu.books.presentation.details.BookDetailsRoute
import com.mantasbu.books.presentation.home.HomeRoute
import com.mantasbu.books.presentation.lists.BookListRoute
import com.mantasbu.books.presentation.ui.theme.BooksTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BooksTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                ) {
                    composable("home") {
                        HomeRoute(
                            onOpenList = { listId ->
                                navController.navigate("list/$listId")
                            },
                            onOpenBook = { bookId ->
                                navController.navigate("book/$bookId")
                            },
                        )
                    }

                    composable(
                        route = "book/{bookId}",
                        arguments = listOf(
                            navArgument("bookId") {
                                type = NavType.IntType
                            },
                        ),
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("bookId") ?: return@composable
                        BookDetailsRoute(
                            bookId = id,
                            onBack = { navController.popBackStack() },
                            onOpenBook = { bid -> navController.navigate("book/$bid") },
                        )
                    }

                    composable(
                        route = "list/{listId}",
                        arguments = listOf(
                            navArgument("listId") {
                                type = NavType.IntType
                            },
                        ),
                    ) { backStackEntry ->
                        val listId = backStackEntry.arguments?.getInt("listId") ?: return@composable
                        BookListRoute(
                            listId = listId,
                            onBack = { navController.popBackStack() },
                            onOpenBook = { bookId ->
                                navController.navigate("book/$bookId")
                            },
                        )
                    }
                }
            }
        }
    }
}