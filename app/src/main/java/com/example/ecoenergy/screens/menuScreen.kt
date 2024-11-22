package com.example.ecoenergy.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ecoenergy.model.ContaEnergia
import com.example.ecoenergy.model.User
import com.example.ecoenergy.viewModel.ContaViewModel
import com.example.ecoenergy.viewModel.MainViewModel
import com.example.ecoenergy.viewModel.ThemeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
fun menuScreen(navController: NavController, themeViewModel: ThemeViewModel, mainViewModel: MainViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mainViewModel: MainViewModel = viewModel()

    MaterialTheme(
        colorScheme = if (themeViewModel.isDarkTheme.collectAsState().value) darkColorScheme() else lightColorScheme()
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    DrawerContent(
                        isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value,
                        onThemeChange = { themeViewModel.toggleTheme() },
                        navController = navController
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopBar(
                        isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value,
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
                ScreenContent(
                    modifier = Modifier.padding(padding),
                    navController = navController,
                    isDarkTheme = themeViewModel.isDarkTheme.collectAsState().value,
                    viewModel = mainViewModel
                )
            }
        }
    }
}

@Composable
fun ScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    viewModel: MainViewModel
) {
    val electricityBills = remember { mutableStateListOf<ContaEnergia>() }
    var progress by remember { mutableStateOf(0f) }
    val context = LocalContext.current
    var showPracticeModal by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val textColor = if (isDarkTheme) Color.White else Color.Black
    var showChangesModal by remember { mutableStateOf(false) }
    var showConsumptionModal by remember { mutableStateOf(false)}
    val contaViewModel: ContaViewModel = viewModel()
    var totalSpent by remember { mutableStateOf(0f) }
    var totalSaved by remember { mutableStateOf(0f) }

    val practices = viewModel.practices
    val mudancas = viewModel.changes
    val consumos = viewModel.consumptions
    val practiceCount = viewModel.practiceCount
    val changesCount  = viewModel.changesCount
    val consumptionCount   = viewModel.consumptionCount

    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("conta_energia")
            .get()
            .addOnSuccessListener { result ->
                val bills = result.mapNotNull { doc ->
                    doc.toObject(ContaEnergia::class.java)
                }

                electricityBills.clear()
                electricityBills.addAll(bills)

                if (bills.size >= 2) {
                    totalSpent = bills.last().value.toFloatOrNull() ?: 0f
                    totalSaved = bills[bills.size - 2].value.toFloatOrNull() ?: 0f

                    progress = if (totalSaved > 0) {
                        calculateProgress(bills)
                    } else {
                        0f
                    }
                } else {
                    progress = 0f
                }
            }
            .addOnFailureListener { exception ->
                println("Erro ao carregar Firestore: ${exception.message}")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 120.dp, bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val progressPercentage = progress * 100
            val isSpending = totalSpent > totalSaved
            val progressColor = if (isSpending) listOf(Color.Red, Color.Red) else listOf(Color.Green, Color.Yellow, Color.Green)

            RoundedCircularProgressIndicator(
                progress = progress.absoluteValue,
                colors = progressColor,
                strokeWidth = 25f,
                modifier = Modifier.size(180.dp)
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%.2f%%", progressPercentage.absoluteValue),
                    style = MaterialTheme.typography.displayMedium,
                    color = textColor
                )
                Text(
                    text = if (isSpending) "Gasto" else "Economizado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor,
                    textAlign = TextAlign.Center
                )

            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "",
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PROGRESSO MENSAL",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ProgressCard(
                title = "PRÁTICAS",
                emoji = "💪",
                count = "$practiceCount/4",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { showPracticeModal = true }
            )
            PracticeModal(
                showModal = showPracticeModal,
                onDismiss = { showPracticeModal = false },
                practices = practices,
                onPracticeDone = { practice ->
                    viewModel.practices.remove(practice)
                    viewModel.practiceCount++ // Atualiza a contagem no ViewModel
                    Toast.makeText(context, "Prática '$practice' concluída!", Toast.LENGTH_SHORT).show()
                }
            )
            ProgressCard(
                title = "MUDANÇAS",
                emoji = "📚",
                count = "$changesCount/4",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { showChangesModal = true }
            )
            ChangesModal(
                showModal = showChangesModal,
                onDismiss = { showChangesModal = false },
                mudancas = mudancas,
                onChangeDone = { mudanca ->
                    mudancas.remove(mudanca)
                    viewModel.changesCount++
                    Toast.makeText(context, "Consumo '$mudanca' concluída!", Toast.LENGTH_SHORT).show()
                }
            )

            ProgressCard(
                title = "CONSUMO",
                emoji = "🧘‍♂️",
                count = "$consumptionCount/4",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { showConsumptionModal = true }
            )
            ConsumptionModal(
                showModal = showConsumptionModal,
                onDismiss = { showConsumptionModal = false },
                consumos = consumos,
                onConsumptionDone = {consumo ->
                    consumos.remove(consumo)
                    viewModel.consumptionCount++
                    Toast.makeText(context, "Consumo '$consumo' concluída!", Toast.LENGTH_SHORT).show()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CONTAS DE ENERGIA",
            style = MaterialTheme.typography.titleMedium,
            color = textColor,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ProgressCard(
                title = "CADASTRAR",
                emoji = "📝",
                count = "",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { showModal = true }
            )
            AddElectricityModal(
                showModal = showModal,
                onDismiss = { showModal = false },
                contaViewModel = contaViewModel,
                onConfirm = { value, month, notes ->
                    saveElectricityBill(

                        value = value,
                        month = month,
                        notes = notes,
                        onSuccess = {
                            Toast.makeText(context, "Conta de luz cadastrada!\nPor favor atualize a tela para ver o quanto economizou.", Toast.LENGTH_SHORT).show()
                        },
                        onError = { exception ->
                            Toast.makeText(context, "Erro: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                    )
                    showModal = false
                }
            )
            ProgressCard(
                title = "CONTAS",
                emoji = "📒",
                count = "",
                isDarkTheme = isDarkTheme,
                modifier = Modifier.weight(1f),
                onClick = { navController.navigate("contas") }
            )
        }
    }
}


@Composable
fun RoundedCircularProgressIndicator(
    progress: Float,
    colors: List<Color>,
    strokeWidth: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sweepAngle = 360 * progress
        val size = size.minDimension
        val center = Offset(size / 2, size / 2)
        val radius = size / 2 - strokeWidth / 2

        val gradient = Brush.sweepGradient(colors)

        drawArc(
            brush = gradient,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = Stroke(strokeWidth, cap = StrokeCap.Round),
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
        )
    }
}


fun calculateProgress(electricityBills: List<ContaEnergia>): Float {
    if (electricityBills.size < 2) return 0f

    val monthMap = mapOf(
        "Janeiro" to 1, "Fevereiro" to 2, "Março" to 3, "Abril" to 4,
        "Maio" to 5, "Junho" to 6, "Julho" to 7, "Agosto" to 8,
        "Setembro" to 9, "Outubro" to 10, "Novembro" to 11, "Dezembro" to 12
    )

    val sortedBills = electricityBills.sortedBy { monthMap[it.month] ?: 0 }
    val lastBill = sortedBills.lastOrNull()?.value?.toFloatOrNull() ?: 0f
    val previousBill = sortedBills.getOrNull(sortedBills.size - 2)?.value?.toFloatOrNull() ?: 0f

    println("Ultima conta: $lastBill, Penúltima conta: $previousBill")

    return if (previousBill > 0f) {
        ((previousBill - lastBill) / previousBill).coerceIn(0f, 1f)
    } else {
        0.5f
    }
}





@Composable
fun lightColorScheme(): ColorScheme {
    return lightColorScheme(
        primary = Color(0xFF6200EE),
        onPrimary = Color.White,
        secondary = Color(0xFF03DAC6),
        background = Color(0xFFF6F6F6),
        onBackground = Color.Black,
        surface = Color.White,
        onSurface = Color.Black
    )
}

@Composable
fun darkColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = Color(0xFFBB86FC),
        onPrimary = Color.Black,
        secondary = Color(0xFF03DAC6),
        background = Color(0xFF121212),
        onBackground = Color.White,
        surface = Color(0xFF1F1F1F),
        onSurface = Color.White
    )
}


@Composable
fun ProgressCard(
    title: String,
    emoji: String,
    count: String,
    isDarkTheme: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White
    val textColor = if (isDarkTheme) Color.White else Color.Black

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .shadow(8.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        ),

        contentPadding = PaddingValues(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = textColor)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = count, style = MaterialTheme.typography.titleMedium, color = textColor)
        }
    }
}

@Composable
fun AddElectricityModal(
    showModal: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    contaViewModel: ContaViewModel
) {
    if (showModal) {
        var value by remember { mutableStateOf("") }
        var selectedMonth by remember { mutableStateOf("") }
        var notes by remember { mutableStateOf("") }
        var expanded by remember { mutableStateOf(false) }

        val months = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    contaViewModel.addEnergyBill(
                        ContaEnergia(
                            id = 0,
                            value = value,
                            month = selectedMonth,
                            notes = notes,

                        )
                    )
                    onConfirm(value, selectedMonth, notes)

                }) {
                    Text("Salvar", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            title = { Text(text = "Cadastre sua Conta de Luz") },
            text = {
                Column {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { value = it },
                        label = { Text("Valor") },
                        placeholder = { Text("Ex: 100.00") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = selectedMonth,
                            onValueChange = {},
                            label = { Text("Mês") },
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expanded)
                                        androidx.compose.material.icons.Icons.Filled.ArrowDropUp
                                    else
                                        androidx.compose.material.icons.Icons.Filled.ArrowDropDown,
                                    contentDescription = null
                                )
                            },
                            enabled = false
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            months.forEach { month ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedMonth = month
                                        expanded = false
                                    },
                                    text = { Text(month) }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Observações") },
                        placeholder = { Text("Adicione detalhes opcionais") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        )
    }
}


fun saveElectricityBill(value: String, month: String, notes: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val electricityBill = mapOf(
        "value" to value.toString(),
        "month" to month,
        "notes" to notes,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("conta_energia")
        .add(electricityBill)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { exception -> onError(exception) }
}
@Composable
fun PracticeModal(
    showModal: Boolean,
    onDismiss: () -> Unit,
    practices: List<String>,
    onPracticeDone: (String) -> Unit
) {
    if (showModal) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()

                ){
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            title = { Text("Práticas para economizar") },
            text = {
                Column {
                    practices.forEach { practice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onPracticeDone(practice)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = practice, modifier = Modifier.weight(1f))
                            Text(
                                text = "✔️",
                                textAlign = TextAlign.End,
                                color = Color.Green,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
@Composable
fun ConsumptionModal(
    showModal: Boolean,
    onDismiss: () -> Unit,
    consumos: List<String>,
    onConsumptionDone: (String) -> Unit
) {
    if (showModal) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()

                ){
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            title = { Text("Dispositivos que consomem energia e devem ser monitorados.") },
            text = {
                Column {
                    consumos.forEach { consumo ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onConsumptionDone(consumo)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = consumo, modifier = Modifier.weight(1f))
                            Text(
                                text = "✔️",
                                textAlign = TextAlign.End,
                                color = Color.Green,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}
@Composable
fun ChangesModal(
    showModal: Boolean,
    onDismiss: () -> Unit,
    mudancas: List<String>,
    onChangeDone: (String) -> Unit
) {
    if (showModal) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Row (
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()

                ){
                    TextButton(onClick = onDismiss) {
                        Text("Fechar", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            },
            title = { Text("Mudanças") },
            text = {
                Column {
                    mudancas.forEach { mudanca ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    onChangeDone(mudanca)
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = mudanca, modifier = Modifier.weight(1f))
                            Text(
                                text = "✔️",
                                textAlign = TextAlign.End,
                                color = Color.Green,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

