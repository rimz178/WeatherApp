package fi.tuni.weatherapp.service

import fi.tuni.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory



object WeatherApiService {


    private var retrofit:Retrofit?=null
    private var BASE_URL="https://api.openweathermap.org/data/2.5/"
    fun getWeatherApi():WeatherApi? {
        if(retrofit==null) {
            retrofit=Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
        }
        return retrofit!!.create(WeatherApi::class.java)
    }

}
