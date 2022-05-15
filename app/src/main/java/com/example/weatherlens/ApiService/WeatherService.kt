package com.example.weatherlens.ApiService

import com.example.weatherlens.respond.WeatherDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather")
    fun getWeatherDetails(
        @Query("q") q: String,
        @Query("appid") appid: String
    ): Call<WeatherDetails>

}