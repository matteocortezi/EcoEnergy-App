package com.example.ecoenergy.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ecoenergy.model.ContaEnergia
import com.example.ecoenergy.viewModel.ContaViewModel
import com.example.ecoenergy.viewModel.ThemeViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat

@Composable
fun contasScreen(
    navController: NavController,
    contaViewModel: ContaViewModel,
    themeViewModel: ThemeViewModel,
) {
    val scope = rememberCoroutineScope()
    val energyBills by contaViewModel.energyBills.collectAsState(initial = emptyList())
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()

    var selectedBill by remember { mutableStateOf<ContaEnergia?>(null) }
    var isEditDialogOpen by remember { mutableStateOf(false) }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
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
                        isDarkTheme = isDarkTheme,
                        onThemeChange = { themeViewModel.toggleTheme() },
                        OnOpenDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        navController = navController
                    )
                }
            ) { paddingValues ->
                if (energyBills.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Nenhuma conta cadastrada.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(energyBills) { bill ->
                            ContaCard(
                                bill = bill,
                                onEditClick = {
                                    selectedBill = bill
                                    isEditDialogOpen = true
                                },
                                onDeleteClick = { bill ->
                                    contaViewModel.removeEnergyBill(bill.id.toString())
                                },
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                }
            }
        }

        if (isEditDialogOpen && selectedBill != null) {
            EditBillDialog(
                bill = selectedBill!!,
                onDismiss = { isEditDialogOpen = false },
                onSave = { editedBill ->
                    contaViewModel.updateEnergyBill(editedBill)
                    isEditDialogOpen = false
                }
            )
        }
    }
}

@Composable
fun EditBillDialog(
    bill: ContaEnergia,
    onDismiss: () -> Unit,
    onSave: (ContaEnergia) -> Unit
) {
    var updatedValue by rememberSaveable { mutableStateOf(bill.value.toString()) }
    var updatedNotes by rememberSaveable { mutableStateOf(bill.notes) }
    var updatedMonth by rememberSaveable { mutableStateOf(bill.month) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {

                    onSave(
                        bill.copy(
                            value = updatedValue,
                            notes = updatedNotes,
                            month = updatedMonth
                        )
                    )

            }) {
                Text("Salvar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = MaterialTheme.colorScheme.onBackground)
            }
        },
        title = { Text("Editar Conta de Energia") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = updatedValue,
                    onValueChange = { updatedValue = it },
                    label = { Text("Valor") }
                )
                OutlinedTextField(
                    value = updatedNotes,
                    onValueChange = { updatedNotes = it },
                    label = { Text("Observações") }
                )
                MonthDropdownMenu(
                    selectedMonth = updatedMonth,
                    onMonthSelected = { updatedMonth = it }
                )
            }
        }
    )
}

@Composable
fun MonthDropdownMenu(
    selectedMonth: String,
    onMonthSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val months = listOf(
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
    )

    Box {
        OutlinedTextField(
            value = selectedMonth,
            onValueChange = {},
            label = { Text("Mês") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = { Text(month) },
                    onClick = {
                        onMonthSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ContaCard(
    bill: ContaEnergia,
    onEditClick: (ContaEnergia) -> Unit,
    onDeleteClick: (ContaEnergia) -> Unit,
    isDarkTheme: Boolean,
) {
    val formattedValue = try {
        val valueAsDouble = bill.value.toDoubleOrNull()
        if (valueAsDouble != null) {
            NumberFormat.getCurrencyInstance().format(valueAsDouble)
        } else {
            "Valor inválido"
        }
    } catch (e: IllegalArgumentException) {
        "Valor inválido"
    }

    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.LightGray

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Valor: $formattedValue", style = MaterialTheme.typography.titleMedium)
            Text("Mês: ${bill.month}", style = MaterialTheme.typography.bodyMedium)
            if (bill.notes.isNotEmpty()) {
                Text("Observações: ${bill.notes}", style = MaterialTheme.typography.bodySmall)
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(
                    onClick = { onEditClick(bill) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { onDeleteClick(bill) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
