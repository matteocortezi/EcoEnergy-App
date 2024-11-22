package com.example.ecoenergy.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class ContaEnergia(
    val id: Int = 0,
    val value: String = "",
    val month: String = "",
    val notes: String = "",
)




