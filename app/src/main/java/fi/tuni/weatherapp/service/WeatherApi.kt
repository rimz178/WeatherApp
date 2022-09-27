package fi.tuni.weatherapp.service

import fi.tuni.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query





interface WeatherApi {

    @GET("data/2.5/weather?/&units=metric&lang=en&appid=1824f0b34ad8109e42bc1e6e5fb99606")


    fun getData(
        @Query( "q") cityName : String
    ): Single<WeatherModel>

    fun getWeatherData(
        @Query(value = "latitude") lat: Double,
        @Query(value = "longitude") lon : Double
    ): Single<WeatherModel>
}

