package com.serega.requestmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serega.requestmanager.ui.profile.CreateRequestScreen
import com.serega.requestmanager.ui.profile.RequestListScreen
import com.serega.requestmanager.ui.theme.RequestManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RequestManagerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "requestList"
    ) {
        composable("requestList") {
            RequestListScreen(
                onCreateNew = {
                    navController.navigate("createRequest")
                }
            )
        }

        composable("createRequest") {
            CreateRequestScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}