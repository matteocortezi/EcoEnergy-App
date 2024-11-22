package com.example.ecoenergy.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecoenergy.model.ContaEnergia
import com.example.ecoenergy.model.User
import com.example.ecoenergy.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


fun getCurrentUserId(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
}

suspend fun fetchUserData(userId: String): User {
    val db = FirebaseFirestore.getInstance()
    val document = db.collection("users").document(userId).get().await()
    return document.toObject(User::class.java) ?: User()
}



class UserViewModel : ViewModel() {
    val _userData = MutableLiveData<User>()
    val userData: LiveData<User?> get() = _userData

    fun fetchUserData() {
        viewModelScope.launch {
            val userId = getCurrentUserId()
            if (userId != null) {
                val userData = fetchUserData(userId)
                _userData.value = userData
            }
        }
    }
    private val _user = mutableStateListOf<User>()
    val user: List<User> get() = _user

    init {
        loadUser()
    }

    fun loadUser() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getUser()
                if (response.isSuccessful) {
                    response.body()?.let { userList ->
                        _user.clear()
                        _user.addAll(userList)
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Erro ao carregar tarefas: ${e.message}")
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.createUser(user)
                if (response.isSuccessful) {
                    response.body()?.let { newUser ->
                        _user.add(newUser)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.updateUser(user.id, user)
                if (response.isSuccessful) {
                    response.body()?.let { updateUser ->
                        val index = _user.indexOfFirst { it.id == updateUser.id }
                        if (index != -1) {
                            _user[index] = updateUser
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Erro ao atualizar tarefa: ${e.message}")
            }
        }
    }

    fun removeUser(userId: Int) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.deleteUser(userId)
                if (response.isSuccessful) {
                    _user.removeAll { it.id == userId }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Erro ao remover tarefa: ${e.message}")
            }
        }
    }
}

