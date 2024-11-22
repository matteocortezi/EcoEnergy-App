package com.example.ecoenergy.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecoenergy.model.ContaEnergia
import com.example.ecoenergy.network.RetrofitInstance
import com.example.ecoenergy.network.RetrofitInstance.api
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class ContaViewModel : ViewModel() {

    private val _energyBills = MutableStateFlow<List<ContaEnergia>>(emptyList())
    val energyBills: StateFlow<List<ContaEnergia>> get() = _energyBills
    private val firebaseFirestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val _energyBill = MutableStateFlow<ContaEnergia?>(null)
    val energyBill: StateFlow<ContaEnergia?> get() = _energyBill

    init {
        loadEnergyBills()
    }

    fun loadEnergyBills() {
        viewModelScope.launch {
            val response = try {
                RetrofitInstance.api.getEnergyBills()
            } catch (e: Exception) {
                Log.e("ContaViewModel", "Erro ao carregar contas: ${e.message}")
                null
            }

            if (response?.isSuccessful == true) {
                _energyBills.value = response.body() ?: emptyList()
            } else {
                Log.e("ContaViewModel", "Falha ao obter contas de energia")
            }
        }
    }

    fun addEnergyBill(bill: ContaEnergia) {
        viewModelScope.launch {
            try {
                // Faz a chamada à API para criar a nova conta de energia
                val response = RetrofitInstance.api.createEnergyBill(bill)

                if (response.isSuccessful) {
                    // Recupera a conta criada retornada pela API
                    val newBill = response.body()
                    if (newBill != null) {
                        // Atualiza o StateFlow `_energyBills`
                        _energyBills.update { bills -> bills + newBill }
                    }
                } else {
                    Log.e("ContaViewModel", "Falha ao adicionar conta: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ContaViewModel", "Erro ao adicionar conta: ${e.message}")
            }
        }
    }


    fun updateEnergyBill(updatedBill: ContaEnergia) {
        viewModelScope.launch {
            try {
                api.updateEnergyBill(updatedBill.id, updatedBill) // Certifique-se de usar o ID correto.

                firebaseFirestore.collection("conta_energia")
                    .document(updatedBill.id.toString())
                    .set(updatedBill)
                _energyBills.update { bills ->
                    bills.map { if (it.id == updatedBill.id) updatedBill else it }
                }
            } catch (e: Exception) {
                Log.e("ContaViewModel", "Erro ao atualizar a conta: ${e.message}")
            }
        }
    }

    fun removeEnergyBill(billId: String) {
        viewModelScope.launch {
            try {
                // Remova na API
                api.deleteEnergyBill(billId.toInt())

                firebaseFirestore.collection("conta_energia")
                    .document(billId)
                    .delete()
                _energyBills.update { bills ->
                    bills.filterNot { it.id.toString() == billId }
                }
            } catch (e: Exception) {
                Log.e("ContaViewModel", "Erro ao deletar a conta: ${e.message}")
            }
        }
    }



}
