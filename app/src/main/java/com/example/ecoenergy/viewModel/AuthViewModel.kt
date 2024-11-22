package com.example.ecoenergy.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel(){

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState : LiveData<AuthState> = _authState


    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){}

    fun login(email : String, password : String){

        if (email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email ou senha estão incorretos")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }else{
                    _authState.value = AuthState.Error(
                        task.exception?.message ?: "Dados preenchidos incorretamente"
                    )
                }
            }
    }
    fun cadastro(email: String, password: String, nome: String, telefone: String, dtNascimento: String) {
        if (email.isEmpty() || password.isEmpty() || nome.isEmpty() || telefone.isEmpty() || dtNascimento.isEmpty()) {
            _authState.value = AuthState.Error("Cadastro inválido")
            return
        }

        _authState.value = AuthState.Loading
        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid

                    if (userId != null) {
                        val firestore = FirebaseFirestore.getInstance()

                        val userData = hashMapOf(
                            "nome" to nome,
                            "telefone" to telefone,
                            "dtNascimento" to dtNascimento,
                            "email" to email
                        )

                        firestore.collection("users")
                            .document(userId)
                            .set(userData)
                            .addOnSuccessListener {
                                _authState.value = AuthState.Authenticated
                            }
                            .addOnFailureListener { e ->
                                _authState.value =
                                    AuthState.Error("Erro ao salvar dados adicionais: ${e.message}")
                            }
                    } else {
                        _authState.value = AuthState.Error("Falha ao recuperar o ID do usuário")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Erro no cadastro")
                }
            }
    }

}
sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message : String) : AuthState()


}