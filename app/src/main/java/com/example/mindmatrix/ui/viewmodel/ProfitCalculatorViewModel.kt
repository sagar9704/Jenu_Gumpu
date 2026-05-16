package com.example.mindmatrix.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mindmatrix.network.MarketPrice
import com.example.mindmatrix.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfitCalculatorViewModel : ViewModel() {
    private val _prices = mutableStateOf<List<MarketPrice>>(emptyList())
    val prices: State<List<MarketPrice>> = _prices

    fun fetchPrices() {
        RetrofitClient.instance.getMarketPrices().enqueue(object : Callback<List<MarketPrice>> {
            override fun onResponse(call: Call<List<MarketPrice>>, response: Response<List<MarketPrice>>) {
                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    _prices.value = response.body()!!
                } else {
                    _prices.value = getMockPrices()
                }
            }
            override fun onFailure(call: Call<List<MarketPrice>>, t: Throwable) {
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

    fun calculateProfit(quantity: Double, pricePerKg: Double, cost: Double): Double {
        return (quantity * pricePerKg) - cost
    }
}
