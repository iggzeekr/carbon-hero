package com.example.carbonhero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.carbonhero.screens.*
import com.example.carbonhero.ui.theme.CarbonHeroTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarbonHeroTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login_screen" // 👈 her zaman login ile başla
                    ) {
                        composable("login_screen") { LoginScreen(navController) }
                        composable("register_screen") { RegisterScreen(navController) }
                        composable("user_data_screen") { UserDataScreen(navController) }
                        composable("main_screen") { MainScreen(navController) }
                    }
                }
            }
        }
    }
}
