package com.example.ecoenergy.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ecoenergy.R
import com.example.ecoenergy.viewModel.AuthState
import com.example.ecoenergy.viewModel.AuthViewModel
import com.example.ecoenergy.viewModel.ThemeViewModel
import com.example.ecoenergy.viewModel.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun perfilScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { themeViewModel.toggleTheme() },
                        navController = navController
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopBar(
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { themeViewModel.toggleTheme() },
                        OnOpenDrawer = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        navController = navController
                    )
                }
            ) { padding ->
                ScreenPerfilContent(
                    modifier = Modifier.padding(padding),
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    authViewModel = viewModel(),
                    onThemeChange = { themeViewModel.toggleTheme() },
                    userViewModel = viewModel()
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenPerfilContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    onThemeChange: () -> Unit
) {
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current
    val userData by userViewModel.userData.observeAsState()
    val textoColor = if (isDarkTheme) Color.White else Color.Black
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    val buttonClr = if (isDarkTheme) Color.DarkGray else Color.White

    val borda = if (isDarkTheme) Color.White else Color.DarkGray

    val nascimento = maskDataNascimento(userData?.dtNascimento ?: "")

    LaunchedEffect(Unit) {
        userViewModel.fetchUserData()
    }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(backgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Email",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor
            )
            Text(
                text = userData?.email ?: "N/A",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borda,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            )

            Text(
                text = "Nome",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor
            )
            Text(
                text = userData?.nome ?: "N/A",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borda,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            )

            Text(
                text = "Telefone",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor
            )
            Text(
                text = maskTelefone(userData?.telefone ?: "N/A"),
                style = TextStyle(fontSize = 16.sp),
                color = textoColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borda,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            )


            Text(
                text = "Data de Nascimento",
                style = TextStyle(fontSize = 16.sp),
                color = textoColor
            )
            Text(
                text = nascimento,
                style = TextStyle(fontSize = 16.sp),
                color = textoColor,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(300.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(backgroundColor)
                    .border(
                        width = 1.dp,
                        color = borda,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(8.dp)
            )

            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonClr,
                    contentColor = textoColor
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.Reply,
                    tint = textoColor,
                    contentDescription = "Voltar",
                )
                Text(text = "Voltar", color = textoColor)
            }
        }
    }
}

fun maskDataNascimento(input: String): String {
    val cleanString = input.replace("[^0-9]".toRegex(), "") // Remove qualquer coisa que não seja número

    // Verificar o tamanho da string antes de tentar aplicar a máscara
    return when {
        cleanString.length <= 2 -> cleanString // Exibe somente os dois primeiros caracteres
        cleanString.length <= 4 -> "${cleanString.substring(0, 2)}/${cleanString.substring(2)}" // Exibe os quatro primeiros caracteres com a máscara
        cleanString.length in 5..7 -> "${cleanString.substring(0, 2)}/${cleanString.substring(2, 4)}/${cleanString.substring(4, cleanString.length)}" // Aplica a máscara parcialmente se tiver entre 5 e 7 caracteres
        cleanString.length == 8 -> "${cleanString.substring(0, 2)}/${cleanString.substring(2, 4)}/${cleanString.substring(4, 8)}" // Aplica a máscara completa
        else -> cleanString // Caso a string tenha mais de 8 caracteres, retorna a string limpa
    }
}
fun maskTelefone(input: String): String {
    val cleanString = input.replace("[^0-9]".toRegex(), "") // Remove qualquer coisa que não seja número

    // Verificar o tamanho da string antes de aplicar a máscara
    return when {
        cleanString.length <= 2 -> cleanString // Exibe apenas os dois primeiros caracteres
        cleanString.length <= 6 -> "(${cleanString.substring(0, 2)}) ${cleanString.substring(2)}" // Aplica a máscara para o número de telefone até 6 caracteres
        cleanString.length <= 10 -> "(${cleanString.substring(0, 2)}) ${cleanString.substring(2, 6)}-${cleanString.substring(6)}" // Aplica a máscara até 10 caracteres
        cleanString.length == 11 -> "(${cleanString.substring(0, 2)}) ${cleanString.substring(2, 7)}-${cleanString.substring(7, 11)}" // Aplica a máscara completa para 11 caracteres
        else -> cleanString // Caso tenha mais de 11 caracteres, retorna a string limpa
    }
}





