package fi.tuni.weatherapp.service


import fi.tuni.weatherapp.model.WeatherModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather?/&units=metric&appid=9853ec5ecf367cb385919fb2caf40185")

    fun getData(
        @Query(value = "q") cityName : String
    ): Single<WeatherModel>
}

