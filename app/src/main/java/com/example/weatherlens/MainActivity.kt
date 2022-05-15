package com.example.weatherlens


import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherlens.ApiService.WeatherService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

class MainActivity : AppCompatActivity() {

    private val TAG = "Main activity"
    private val appId = "9da6ee5d7126194e4330f568c0a97467"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchPlace.setOnClickListener {
            date.visibility = View.INVISIBLE
            layout_placeName.visibility = View.VISIBLE
            applyPlace.visibility = View.VISIBLE

        }
        applyPlace.setOnClickListener {
            pBar.visibility = View.VISIBLE
            val str = txt_placeName.text.toString().trim()
            if (str.isEmpty()) {
                layout_placeName?.error = "Field can't be empty"
                pBar.visibility = View.GONE
            } else {
                giveResult(str)
            }
        }

    }

    private fun giveResult(str: String) {
        val api = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = api.getWeatherDetails(str, appId).awaitResponse()
                if (response.isSuccessful) {
                    layout_placeName.visibility = View.INVISIBLE
                    applyPlace.visibility = View.INVISIBLE
                    date.visibility = View.VISIBLE
                    pBar.visibility = View.GONE
                    val data = response.body()
                    Log.i(TAG, data.toString())

                    //  get current date
                    val currentDate = getCurrentDate()
                    date.text = currentDate.toString() // setting date

                    placeName.text = str // setting place name

                    // change temp from kelvin to Celsius
                    val day = data?.main?.temp_max?.minus(273.15) //DayTemp
                    // setting day temp
                    val d = String.format("%.2f", day)
                    maxTemp.text = d

                    // change temp from kelvin to Celsius
                    val night = data?.main?.temp_min?.minus(273.15) //NightTemp
                    // setting night temp
                    val n = String.format("%.2f", night)
                    minTemp.text = n


                    val currentTemp = data?.main?.temp?.minus(273.15) //current Temp
                    // setting current temp
                    val cT = String.format("%.2f", currentTemp)
                    temp.text = cT

                    // setting description
                    val des = data?.weather?.get(0)?.description
                    description.text = des

                    // setting humidity
                    val hum = data?.main?.humidity
                    humidity.text = hum.toString() + "%"

                    //setting wind
                    val win = data?.wind?.speed
                    wind.text = win.toString() + "mps"

                    //setting pressure
                    val press = data?.main?.pressure
                    pressure.text = press.toString() + "hpa"

                    txt_placeName.text?.clear()

                } else {
                    pBar.visibility = View.GONE
                    Toast.makeText(
                        applicationContext,
                        "Please only enter city/state/country name",
                        Toast.LENGTH_LONG
                    ).show()
                    txt_placeName.text?.clear()

                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.i(TAG, "giveResult: ${e.localizedMessage}")
                    Toast.makeText(applicationContext, "something went wrong", Toast.LENGTH_SHORT)
                        .show()
                    pBar.visibility = View.GONE
                    txt_placeName.text?.clear()
                }
            }
        }
    }


    private fun getCurrentDate(): Any {
        val c: Date = Calendar.getInstance().getTime()
        val df = SimpleDateFormat("MMM dd,yyyy", Locale.getDefault())
        val formattedDate: String = df.format(c)

        return formattedDate
    }

    // hide keyboard
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}