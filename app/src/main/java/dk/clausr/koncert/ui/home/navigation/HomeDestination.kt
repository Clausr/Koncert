package dk.clausr.koncert.ui.home.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dk.clausr.koncert.navigation.KoncertNavigationDestination
import dk.clausr.koncert.ui.chat.navigation.navigateToChatRoom
import dk.clausr.koncert.ui.home.HomeRoute

object HomeDestination : KoncertNavigationDestination {
    override val route: String = "home_route"
    override val destination: String = "home_destination"
}

fun NavGraphBuilder.homeGraph(
    windowSizeClass: WindowSizeClass,
    navController: NavController,
) {
    composable(route = HomeDestination.route) {
        HomeRoute(
            onNavigateToChat = {
                navController.navigateToChatRoom(it)
            },
        )
    }
}