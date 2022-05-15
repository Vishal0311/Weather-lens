package com.example.weatherlens.respond

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)