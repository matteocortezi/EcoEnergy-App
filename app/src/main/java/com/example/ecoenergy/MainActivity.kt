package com.example.ecoenergy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecoenergy.viewModel.ThemeViewModel
import com.example.ecoenergy.ui.theme.EcoEnergyTheme
import kotlinx.coroutines.delay
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cadastroScreen

import com.example.ecoenergy.viewModel.AuthViewModel
import com.example.ecoenergy.screens.contasScreen
import com.example.ecoenergy.screens.digScreen
import com.example.ecoenergy.screens.loginScreen
import com.example.ecoenergy.screens.menuScreen
import com.example.ecoenergy.screens.perfilScreen
import com.example.ecoenergy.viewModel.UserViewModel


class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
            EcoEnergyTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val userViewModel: UserViewModel = viewModel()
                    val drawerState = rememberDrawerState(DrawerValue.Closed)

                    NavHost(
                        navController = navController,
                        startDestination = "splash"
                    ) {
                        composable(route = "splash") {
                            SplashScreen {
                                navController.navigate("login") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                        composable(route = "login") {
                            loginScreen(navController, authViewModel)
                        }
                        composable(route = "cadastro") {
                            cadastroScreen(navController, userViewModel, authViewModel)
                        }
                        composable(route = "home") {
                            menuScreen(navController, themeViewModel, mainViewModel = viewModel())
                        }
                        composable(route = "perfil") {
                            perfilScreen(navController, themeViewModel)
                        }
                        composable(route = "contas") {
                            contasScreen(
                                navController = navController,
                                contaViewModel = viewModel(),
                                themeViewModel = themeViewModel

                            )
                        }
                        composable(route = "digite") {
                            digScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }

    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_eco),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .scale(scale.value)
                .padding(32.dp),
            contentScale = ContentScale.Fit
        )
    }
}
