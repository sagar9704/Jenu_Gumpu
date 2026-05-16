package com.example.mindmatrix.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    // Authentication
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/register")
    fun register(@Body user: User): Call<User>

    @POST("api/logout")
    fun logout(): Call<LogoutResponse>

    // Profile
    @GET("api/profile")
    fun getProfile(): Call<User>

    @PUT("api/profile")
    fun updateProfile(@Body user: User): Call<User>

    // Harvest
    @GET("api/harvest")
    fun getHarvestLogs(): Call<List<HarvestLog>>

    @POST("api/harvest")
    fun addHarvestLog(@Body log: HarvestLog): Call<HarvestLog>

    @GET("api/collective-stock")
    fun getCollectiveStock(): Call<StockResponse>

    @GET("api/market-prices")
    fun getMarketPrices(): Call<List<MarketPrice>>
}

data class LoginRequest(val phone: String, val password: String)
data class LoginResponse(val success: Boolean, val user: User?)
data class LogoutResponse(val success: Boolean)

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val phone: String,
    val password: String? = null,
    val location: String
)

data class HarvestLog(
    val id: Long? = null,
    val date: Long,
    val location: String,
    val quantity: Double,
    val floralSource: String,
    val grade: String,
    val batchId: String
)

data class StockResponse(
    val totalQuantity: Double,
    val unit: String,
    val entryCount: Int
)

data class MarketPrice(
    val source: String,
    val price: Double,
    val unit: String
)
