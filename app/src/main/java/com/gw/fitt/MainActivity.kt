package com.gw.fitt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gw.fitt.navigation.FittBottomNav
import com.gw.fitt.navigation.FittNavGraph
import com.gw.fitt.ui.theme.FittTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FittTheme {
                FittApp()
            }
        }
    }
}

@Composable
private fun FittApp() {
    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { FittBottomNav(navController = navController) }
    ) { innerPadding ->
        FittNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
