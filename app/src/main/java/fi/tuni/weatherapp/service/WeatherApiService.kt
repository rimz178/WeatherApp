package fi.tuni.weatherapp.service

import fi.tuni.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory



class WeatherApiService {
    private val  BASE_URL ="https://api.openweathermap.org/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(WeatherApi::class.java)

    fun getDataService(cityName :String): Single<WeatherModel> {
        return api.getData(cityName)

    }
    fun getCity(lat: Double ,lon : Double  ) : Single<WeatherModel> {
        return  api.getWeatherData(lat,lon)
    }

}
