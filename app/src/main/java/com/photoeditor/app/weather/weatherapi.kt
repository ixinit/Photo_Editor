package com.photoeditor.app.weather

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import ja.burhanrashid52.photoeditor.PhotoEditor
import org.json.JSONException


class WeatherApi {

    private val context:Context
    var Result = 0

    lateinit var weather_description:String
    lateinit var city:String
    lateinit var temp:String

    constructor(context: Context){
        this.context=context
    }

    fun getWeather(mPhotoEditor: PhotoEditor?){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        val url = "https://api.openweathermap.org/data/2.5/weather?q=Smolensk&units=metric&lang=ru&appid=e2b1ff02879860e3234292aa90aa59dc"

        // Request a json response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    weather_description = response.getJSONArray("weather").getJSONObject(0).getString("description")
                    city = response.getString("name")
                    temp = response.getJSONObject("main").getString("temp")
                    //Result=1
                    mPhotoEditor?.addText("${weather_description}\n${city}\nТемпература: ${temp}",Color.BLACK);
                }catch (e:JSONException){
                    e.printStackTrace()
                    mPhotoEditor?.addText("Не удалось получть данные о погоде",Color.BLACK);
                }
            },
            { error ->
                error.printStackTrace()
            }
        )
        // Add the request to the RequestQueue.
        queue.add(request)
    }

}