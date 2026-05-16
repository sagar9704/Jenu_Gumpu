package com.example.mindmatrix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mindmatrix.network.MarketPrice
import com.example.mindmatrix.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketPriceMonitorViewModel : ViewModel() {
    private val _prices = mutableStateOf<List<MarketPrice>>(emptyList())
    val prices: State<List<MarketPrice>> = _prices

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    fun fetchPrices() {
        _isLoading.value = true
        RetrofitClient.instance.getMarketPrices().enqueue(object : Callback<List<MarketPrice>> {
            override fun onResponse(call: Call<List<MarketPrice>>, response: Response<List<MarketPrice>>) {
                _isLoading.value = false
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _prices.value = response.body()!!
                } else {
                    // Fallback to mock data for demonstration
                    _prices.value = getMockPrices()
                }
            }

            override fun onFailure(call: Call<List<MarketPrice>>, t: Throwable) {
                _isLoading.value = false
                // Fallback to mock data on network error
                _prices.value = getMockPrices()
            }
        })
    }

    private fun getMockPrices(): List<MarketPrice> {
        return listOf(
            MarketPrice("Bengaluru (APMC)", 450.0, "kg"),
            MarketPrice("Mysuru Market", 425.0, "kg"),
            MarketPrice("Kodagu Cooperative", 480.0, "kg"),
            MarketPrice("National Retail Avg", 550.0, "kg")
        )
    }
}
