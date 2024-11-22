import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ecoenergy.R
import androidx.compose.ui.text.input.TextFieldValue
import com.example.ecoenergy.model.User
import com.example.ecoenergy.viewModel.AuthViewModel
import com.example.ecoenergy.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cadastroScreen(navController: NavController, userViewModel: UserViewModel, authViewModel: AuthViewModel) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dtNascimento by remember { mutableStateOf(TextFieldValue("")) }
    var telefone by remember { mutableStateOf(TextFieldValue("")) }
    var confirmeSuaSenha by remember { mutableStateOf("") }
    val authState = authViewModel.authState.observeAsState()


    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().height(400.dp).background(colorResource(id = R.color.white))) {
            Image(
                painter = painterResource(id = R.drawable.logo_eco),
                contentDescription = "Logo do app",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .padding(vertical = 12.dp)
                    .align(Alignment.Center)
                    .offset(y = (-70).dp)
            )
        }

        Card(
            modifier = Modifier
                .width(360.dp)
                .height(720.dp)
                .align(Alignment.BottomCenter)
                .border(
                    BorderStroke(3.dp, colorResource(id = R.color.colorHeader)),
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                ),
            colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.white)),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
            shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 40.dp, top = 10.dp, end = 40.dp, bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                // Nome
                Text("Nome", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = nome,
                    onValueChange = { nome = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("Ex: João Silva") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // E-mail
                Text("Email", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("Ex: professor@gmail.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // Data de Nascimento
                Text("Data de Nascimento", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = dtNascimento,
                    onValueChange = { newValue ->
                        dtNascimento = maskInputWithCursor(newValue, ::maskDtNascimento)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("DD/MM/AAAA") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // Telefone
                Text("Telefone", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = telefone,
                    onValueChange = { newValue ->
                        telefone = maskInputWithCursor(newValue, ::maskTelefone)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("(XX) XXXXX-XXXX") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                // Senha
                // Senha
                Text("Senha", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("Ex: 123456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

                Text("Confirmar Senha", style = TextStyle(fontSize = 16.sp))
                TextField(
                    value = confirmeSuaSenha,
                    onValueChange = { confirmeSuaSenha = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(width = 1.dp, color = Color.Gray, shape = RoundedCornerShape(20.dp)),
                    placeholder = { Text("Ex: 123456") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(), // Importante para esconder a senha
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                )

            }

            Column(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        userViewModel.addUser(
                            User(
                                id = 0,
                                password = password,
                                telefone = telefone.text,
                                dtNascimento = dtNascimento.text,
                                email = email,
                                nome = nome
                            )
                        )
                        authViewModel.cadastro(email, password, nome, telefone.text, dtNascimento.text)
                        navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.colorHeader)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.width(300.dp)
                ) {
                    Text(text = "Cadastrar")
                }
            }
        }
    }
}

fun maskInputWithCursor(value: TextFieldValue, maskFunction: (String) -> String): TextFieldValue {
    val unmaskedValue = value.text.replace(Regex("[^\\d]"), "")
    val maskedText = maskFunction(unmaskedValue)
    val newCursor = maskFunction(unmaskedValue.take(value.selection.start)).length
    return TextFieldValue(
        text = maskedText,
        selection = TextRange(newCursor)
    )
}

fun maskTelefone(input: String): String {
    return when {
        input.length >= 11 -> "(${input.substring(0, 2)}) ${input.substring(2, 7)}-${input.substring(7, 11)}"
        input.length >= 7 -> "(${input.substring(0, 2)}) ${input.substring(2, 6)}-${input.substring(6)}"
        input.length >= 2 -> "(${input.substring(0, 2)}) ${input.substring(2)}"
        else -> input
    }
}

fun maskDtNascimento(input: String): String {
    return when {
        input.length >= 8 -> "${input.substring(0, 2)}/${input.substring(2, 4)}/${input.substring(4, 8)}"
        input.length >= 4 -> "${input.substring(0, 2)}/${input.substring(2, 4)}/${input.substring(4)}"
        input.length >= 2 -> "${input.substring(0, 2)}/${input.substring(2)}"
        else -> input
    }
}
