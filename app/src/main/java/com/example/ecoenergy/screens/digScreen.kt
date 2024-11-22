package com.example.ecoenergy.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import com.example.ecoenergy.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun digScreen(navController: NavController){
    var email by remember {
        mutableStateOf("")
    }

    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_eco),
                contentDescription = "Logo do app",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center)
                    .offset(y = (-60).dp)
            )
        }


        Card(
            modifier = Modifier
                .width(360.dp)
                .height(310.dp)
                .align(Alignment.Center),

            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),

            shape = RoundedCornerShape(30.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {



                Text(
                    text = "Problemas para entrar" ,
                    fontSize = 20.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Insira seu email," +
                            " para receber o código de verificação para acessar sua conta" ,
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top=10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("") },
                    placeholder = { Text("") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp)),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        isLoading = true
                        sendPasswordResetEmail(email) { success, errorMessage ->
                            isLoading = false
                            if (success) {
                                message = "E-mail de redefinição de senha enviado!"
                            } else {
                                message = "Erro: $errorMessage"
                            }
                        }
                        navController.navigate("login")

                    },
                    modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary),
                    enabled = !isLoading
                ) {
                    Text(text = "Enviar E-mail de Redefinição")
                }
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
                }

                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = if (message.startsWith("Erro")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }


        }
    }
}
fun sendPasswordResetEmail(email: String, onResult: (Boolean, String?) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onResult(true, null)
            } else {
                onResult(false, task.exception?.message)
            }
        }
}

