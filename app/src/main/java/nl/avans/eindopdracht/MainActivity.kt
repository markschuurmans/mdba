package nl.avans.eindopdracht

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import nl.avans.eindopdracht.ui.detail.DetailRoute
import nl.avans.eindopdracht.ui.detail.DetailViewModel
import nl.avans.eindopdracht.ui.home.HomeRoute
import nl.avans.eindopdracht.ui.home.HomeViewModel
import nl.avans.eindopdracht.ui.navigation.AppDestinations
import nl.avans.eindopdracht.ui.theme.EindopdrachtTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EindopdrachtTheme {
                val homeViewModel: HomeViewModel = viewModel()
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = AppDestinations.HOME,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        composable(AppDestinations.HOME) {
                            HomeRoute(
                                viewModel = homeViewModel,
                                onCocktailClick = { cocktailId ->
                                    navController.navigate(AppDestinations.detailRoute(cocktailId))
                                }
                            )
                        }

                        composable(
                            route = AppDestinations.DETAIL_ROUTE,
                            arguments = listOf(
                                navArgument(AppDestinations.COCKTAIL_ID_ARG) {
                                    type = NavType.StringType
                                }
                            )
                        ) { backStackEntry ->
                            val cocktailId = backStackEntry.arguments
                                ?.getString(AppDestinations.COCKTAIL_ID_ARG)
                                .orEmpty()

                            val detailViewModel: DetailViewModel = viewModel(
                                key = "detail_$cocktailId",
                                factory = DetailViewModel.provideFactory(
                                    application = application,
                                    cocktailId = cocktailId
                                )
                            )
                            DetailRoute(viewModel = detailViewModel)
                        }
                    }
                }
            }
        }
    }
}


