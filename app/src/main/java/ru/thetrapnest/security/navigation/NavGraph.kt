package ru.thetrapnest.security.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.thetrapnest.security.ui.screens.PracticeScreen
import ru.thetrapnest.security.ui.screens.ProfileScreen
import ru.thetrapnest.security.ui.screens.ScenarioListScreen
import ru.thetrapnest.security.ui.screens.TheoryScreen
import ru.thetrapnest.security.ui.screens.ResultScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "scenarios"
    ) {
        composable("scenarios") {
            ScenarioListScreen(navController = navController)
        }
        
        composable(
            "theory/{vulnerabilityId}",
            arguments = listOf(navArgument("vulnerabilityId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val vulnerabilityId = backStackEntry.arguments?.getInt("vulnerabilityId") ?: 0
            TheoryScreen(
                vulnerabilityId = vulnerabilityId,
                navController = navController
            )
        }
        
        composable(
            "practice/{vulnerabilityId}",
            arguments = listOf(navArgument("vulnerabilityId") { type = androidx.navigation.NavType.IntType })
        ) { backStackEntry ->
            val vulnerabilityId = backStackEntry.arguments?.getInt("vulnerabilityId") ?: 0
            PracticeScreen(
                vulnerabilityId = vulnerabilityId,
                navController = navController
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
                navController = navController
            )
        }
        
        composable("profile") {
            ProfileScreen(navController = navController)
        }
    }
}