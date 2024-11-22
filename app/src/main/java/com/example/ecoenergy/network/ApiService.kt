package com.example.ecoenergy.network

import com.example.ecoenergy.model.ContaEnergia
import com.example.ecoenergy.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("/user")
    suspend fun getUser(): Response<List<User>>

    @POST("/user")
    suspend fun createUser(@Body user: User): Response<User>

    @PUT("/user/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: User): Response<User>

    @DELETE("/user/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    @GET("/conta")
    suspend fun getEnergyBills(): Response<List<ContaEnergia>>

    @POST("/conta")
    suspend fun createEnergyBill(@Body bill: ContaEnergia): Response<ContaEnergia>

    @PUT("conta/{id}")
    suspend fun updateEnergyBill(@Path("id") id: Int, @Body bill: ContaEnergia): Response<ContaEnergia>

    @DELETE("conta/{id}")
    suspend fun deleteEnergyBill(@Path("id") id: Int): Response<Unit>
}
