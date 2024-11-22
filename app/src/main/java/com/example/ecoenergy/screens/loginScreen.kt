package com.example.ecoenergy.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecoenergy.R
import com.example.ecoenergy.viewModel.AuthState
import com.example.ecoenergy.viewModel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun loginScreen(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState = authViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Authenticated -> navController.navigate("home")
            is AuthState.Error -> Toast.makeText(
                context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT
            ).show()

            else -> Unit
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(colorResource(id = R.color.white))
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_eco),
                contentDescription = "Logo do app",
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center)
                    .offset(y = (-20).dp)
            )
        }

        Card(
            modifier = Modifier
                .width(360.dp)
                .height(250.dp)
                .align(Alignment.Center),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
            shape = RoundedCornerShape(30.dp),
            elevation = CardDefaults.cardElevation(8.dp)

        ) {
            Column(
                modifier = Modifier
                    .padding(top = 10.dp, start = 40.dp, end = 40.dp, bottom = 20.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .height(56.dp),
                    contentAlignment = Alignment.Center
                ) {
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        textStyle = TextStyle(fontSize = 16.sp),
                        placeholder = { Text(text = "Ex: professor@gmail.com", fontSize = 16.sp) },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                        .height(56.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        textStyle = TextStyle(fontSize = 16.sp),
                        placeholder = { Text(text = "Digite sua senha", fontSize = 16.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        label = { Text("Senha") },
                                colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                }


                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Button(
                        onClick = { authViewModel.login(email, password) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorHeader)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Entrar")
                    }
                }
            }
        }
                    Column(
                        modifier = Modifier
                            .padding(top = 16.dp, bottom = 55.dp)
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Button(
                            onClick = { navController.navigate("cadastro") },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorHeader)),
                            modifier = Modifier.width(300.dp)
                        ) {
                            Text(text = "Cadastrar")
                        }

                        Button(
                            onClick = { navController.navigate("digite") },
                            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorHeader)),
                            modifier = Modifier.width(300.dp)
                        ) {
                            Text(text = "Esqueci a senha")
                        }
                    }
            }

    }


