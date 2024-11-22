package com.example.ecoenergy.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecoenergy.R

@Composable
fun DrawerContent(
    navController: NavController,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDarkTheme) MaterialTheme.colorScheme.background else Color.White
    val textColor = if (isDarkTheme) MaterialTheme.colorScheme.onBackground else Color.Black

    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
        ){
            Image(
                painter = painterResource(id = R.drawable.ecoenergy),
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(320.dp)
                    .padding(top = 20.dp, start = 20.dp, bottom = 10.dp)
            )
        }

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Menu",
                    tint = textColor
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 17.sp,
                    color = textColor,
                    modifier = Modifier.padding(16.dp)
                )
            },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.EnergySavingsLeaf,
                    contentDescription = "Contas",
                    tint = textColor
                )
            },
            label = {
                Text(
                    text = "Contas de Energia",
                    fontSize = 17.sp,
                    color = textColor,
                    modifier = Modifier.padding(16.dp)
                )
            },
            selected = false,
            onClick = { navController.navigate("contas") }
        )


        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Filled.WbSunny else Icons.Default.NightsStay,
                    contentDescription = if (isDarkTheme) "Modo Claro" else "Modo Escuro",
                    tint = textColor,
                )
            },
            label = {
                Text(
                    text = if (isDarkTheme) "Modo Claro" else "Modo Escuro",
                    fontSize = 17.sp,
                    color = textColor,
                    modifier = Modifier.padding(16.dp)
                )
            },
            selected = false,
            onClick = { onThemeChange() }
        )
    }
}