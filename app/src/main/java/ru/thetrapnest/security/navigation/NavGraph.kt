package ru.thetrapnest.security.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.thetrapnest.security.ui.screens.AuthScreen
import ru.thetrapnest.security.ui.screens.PracticeScreen
import ru.thetrapnest.security.ui.screens.ProfileScreen
import ru.thetrapnest.security.ui.screens.ResultScreen
import ru.thetrapnest.security.ui.screens.ScenarioListScreen
import ru.thetrapnest.security.ui.screens.TheoryScreen
import ru.thetrapnest.security.viewmodel.SecurityViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: SecurityViewModel
) {
    val currentUser by viewModel.currentUser.collectAsState()

    LaunchedEffect(currentUser?.id) {
        if (currentUser == null) {
            navController.navigate("auth") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            navController.navigate("scenarios") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "auth"
    ) {
        composable("auth") {
            AuthScreen(viewModel = viewModel)
        }

        composable("scenarios") {
            ScenarioListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(
            "theory/{vulnerabilityId}",
            arguments = listOf(navArgument("vulnerabilityId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val vulnerabilityId = backStackEntry.arguments?.getInt("vulnerabilityId") ?: 0
            TheoryScreen(
                vulnerabilityId = vulnerabilityId,
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(
            "practice/{vulnerabilityId}",
            arguments = listOf(navArgument("vulnerabilityId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val vulnerabilityId = backStackEntry.arguments?.getInt("vulnerabilityId") ?: 0
            PracticeScreen(
                vulnerabilityId = vulnerabilityId,
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable(
            "result/{vulnerabilityType}/{vulnerabilityId}?input={input}",
            arguments = listOf(
                navArgument("vulnerabilityType") { type = androidx.navigation.NavType.StringType },
                navArgument("vulnerabilityId") { type = androidx.navigation.NavType.IntType },
                navArgument("input") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val vulnerabilityType = backStackEntry.arguments?.getString("vulnerabilityType") ?: ""
            val vulnerabilityId = backStackEntry.arguments?.getInt("vulnerabilityId") ?: 0
            val userInput = backStackEntry.arguments?.getString("input") ?: ""
            ResultScreen(
                vulnerabilityType = vulnerabilityType,
                vulnerabilityId = vulnerabilityId,
                userInput = userInput,
                navController = navController,
                viewModel = viewModel
            )
        }
        
        composable("profile") {
            ProfileScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
