package com.example.ecoenergy.viewModel

import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MainViewModel : ViewModel() {
    var practices = mutableStateListOf(
        "Desligar luzes e equipamentos quando não estiverem em uso.",
        "Aproveitar a iluminação natural durante o dia.",
        "Utilizar ventiladores ao invés de ar-condicionado quando possível.",
        "Cozinhar com tampas nas panelas."
    )

    var changes = mutableStateListOf(
        "Substituir lâmpadas incandescentes por LED.",
        "Comprar eletrodomésticos com selo de eficiência energética.",
        "Instalar sensores de presença em ambientes de uso eventual.",
        "Investir em isolamento térmico para reduzir uso de climatização."
    )

    var consumptions = mutableStateListOf(
        "Chuveiro elétrico.",
        "Geladeira",
        "Ar-condicionado",
        "Aparelhos em modo stand-by"
    )
    var practiceCount by mutableStateOf(0)
    var changesCount by mutableStateOf(0)
    var consumptionCount by mutableStateOf(0)
}