package nl.avans.eindopdracht

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import nl.avans.eindopdracht.data.ThemePreferenceStore
import nl.avans.eindopdracht.ui.detail.DetailRoute
import nl.avans.eindopdracht.ui.detail.DetailViewModel
import nl.avans.eindopdracht.ui.gallery.GalleryRoute
import nl.avans.eindopdracht.ui.gallery.GalleryViewModel
import nl.avans.eindopdracht.ui.home.HomeRoute
import nl.avans.eindopdracht.ui.home.HomeViewModel
import nl.avans.eindopdracht.ui.navigation.AppDestinations
import nl.avans.eindopdracht.ui.theme.EindopdrachtTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isSettingsSheetVisible by rememberSaveable { mutableStateOf(false) }
            val themePreferenceStore = remember(applicationContext) {
                ThemePreferenceStore(applicationContext)
            }
            val isDarkModeEnabled by themePreferenceStore.isDarkModeEnabled.collectAsState(initial = false)
            val scope = rememberCoroutineScope()

            EindopdrachtTheme(
                darkTheme = isDarkModeEnabled,
                dynamicColor = false
            ) {
                val homeViewModel: HomeViewModel = viewModel()
                val galleryViewModel: GalleryViewModel = viewModel()
                val navController = rememberNavController()
                val navBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry.value?.destination?.route
                val isTopLevel = currentRoute == AppDestinations.HOME ||
                    currentRoute == AppDestinations.GALLERY

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val title = when {
                            currentRoute == AppDestinations.GALLERY -> "Mijn Foto's"
                            currentRoute?.startsWith(AppDestinations.DETAIL) == true -> "Cocktail details"
                            else -> "Cocktail Explorer"
                        }
                        CenterAlignedTopAppBar(
                            title = { Text(title) },
                            navigationIcon = {
                                if (!isTopLevel) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Terug"
                                        )
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    bottomBar = {
                        if (isTopLevel) NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.HOME,
                                onClick = {
                                    navController.navigate(AppDestinations.HOME) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == AppDestinations.GALLERY,
                                onClick = {
                                    navController.navigate(AppDestinations.GALLERY) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Collections,
                                        contentDescription = "Mijn Foto's"
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                ),
                                label = { Text("Mijn Foto's") }
                            )
                            NavigationBarItem(
                                selected = isSettingsSheetVisible,
                                onClick = {
                                    isSettingsSheetVisible = true
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Instellingen"
                                    )
                                },
                                label = { Text("Instellingen") }
                            )
                        }
                    }
                ) { innerPadding ->
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

                        composable(AppDestinations.GALLERY) {
                            GalleryRoute(
                                viewModel = galleryViewModel,
                                onOpenDetails = { cocktailId ->
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
                            DetailRoute(
                                viewModel = detailViewModel,
                                cocktailId = cocktailId
                            )
                        }
                    }
                }

                if (isSettingsSheetVisible) {
                    ModalBottomSheet(
                        onDismissRequest = { isSettingsSheetVisible = false }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Instellingen",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Light / Dark mode",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Switch(
                                checked = isDarkModeEnabled,
                                onCheckedChange = { enabled ->
                                    scope.launch {
                                        themePreferenceStore.setDarkModeEnabled(enabled)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


