package com.example.weather_app_kotlin

import android.content.ContentValues.TAG
import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import com.example.weather_app_kotlin.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    //  7b26c92417fd3678d52eac12dc870222

    private  val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Lucknow")
        SearchCity()
    }

    private fun SearchCity() {
       val searchView=binding.searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query!=null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
              return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
       val retrofit=Retrofit.Builder()
           .addConverterFactory(GsonConverterFactory.create())
           .baseUrl("https://api.openweathermap.org/data/2.5/")
           .build().create(ApiInterface::class.java)

        val response=retrofit.getWeatherData(cityName,"7b26c92417fd3678d52eac12dc870222","metric")
        response.enqueue(object :Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
               if(response.isSuccessful && responseBody!=null){
                   val temperature=responseBody.main.temp.toString()
                   val humidity=responseBody.main.humidity.toString()
                   // Log.d("TAG","onResponse:$temperature")
                   val windSpeed =responseBody.wind.speed
                   val sunRise=responseBody.sys.sunrise.toLong()
                   val sunSet=responseBody.sys.sunset.toLong()
                   val seaLevel=responseBody.main.pressure
                   val condition=responseBody.weather.firstOrNull()?.main?: "unknown"
                   val maxTemp=responseBody.main.temp_max
                   val minTemp=responseBody.main.temp_min


                   binding.temp.text="$temperature °C"
                   binding.weather.text=condition
                   binding.maxTemp.text="Max Temp: $maxTemp °C"
                   binding.minTemp.text="Min Temp: $minTemp °C"
                   binding.humidity.text="$humidity %"
                   binding.windSpeed.text="$windSpeed m/s"
                   binding.sunrise.text="${time(sunRise)}"
                   binding.sunset.text="${time(sunSet)}"
                   binding.sea.text="$seaLevel hPa"
                   binding.condition.text=condition
                   binding.day.text=dayName(System.currentTimeMillis())
                       binding.date.text=date()
                       binding.cityName.text="$cityName"

                   changeImageAccordingToCondition(condition)

               }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImageAccordingToCondition(condition: String) {
       when (condition){
           "clear Sky","Sunny","Clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
           "Partly clouds","Clouds","overcast","Mist","Foggy" ->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }

           "Light Rain","Drizzle","Moderate Rain","SHowers","Heavy Rain" ->{
           binding.root.setBackgroundResource(R.drawable.rain_background)
           binding.lottieAnimationView.setAnimation(R.raw.rain)
           }
           "Light Snow","Moderate Snow","Heavy Snow" ->{
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieAnimationView.setAnimation(R.raw.snow)
           }
           else ->{
                   binding.root.setBackgroundResource(R.drawable.sunny_background)
                   binding.lottieAnimationView.setAnimation(R.raw.sun)
               }
       }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long): String {
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

    private fun dayName(timestamp: Long): String {
     val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}