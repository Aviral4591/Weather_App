package com.example.weather_app_kotlin

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)