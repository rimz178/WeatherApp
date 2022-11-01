package fi.tuni.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Switch
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fi.tuni.weatherapp.BuildConfig
import fi.tuni.weatherapp.BuildConfig.API_KEY
import fi.tuni.weatherapp.R
import fi.tuni.weatherapp.databinding.ActivityMainBinding
import fi.tuni.weatherapp.model.WeatherModel
import fi.tuni.weatherapp.service.WeatherApiService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Error
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId
import java.util.*


class MainActivity : AppCompatActivity() {


    private  lateinit var  fused : FusedLocationProviderClient
    private lateinit var activityMainBinding: ActivityMainBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()
        fused = LocationServices.getFusedLocationProviderClient(this)
        activityMainBinding.mainContent.visibility = View.GONE

        getLocation()


        activityMainBinding.edtCityName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getCity(activityMainBinding.edtCityName.text.toString())
                val view = this.currentFocus

                if (view != null ) {
                    val ss: InputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    ss.hideSoftInputFromWindow(view.windowToken, 0)
                    activityMainBinding.edtCityName.clearFocus()
                }
                true

            } else false
        }

    }

    private fun getCity(cityName: String) {


        activityMainBinding.pbLoading.visibility= VISIBLE
        //
        WeatherApiService.getWeatherApi()!!.getCityWeatherData(cityName, API_KEY).enqueue(object: Callback<WeatherModel>{

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {
                if(response.isSuccessful) {
                    gettData(response.body())
                }

                else {
                    Toast.makeText(applicationContext, "wrong city name ", Toast.LENGTH_SHORT).show()

                }

            }

            override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                Toast.makeText(applicationContext, "on Failure", Toast.LENGTH_SHORT).show()
            }

        })
    }


    @SuppressLint("MissingPermission","SetTextI18n")
    private  fun getLocation() {

        if(checkPermission())
        {
            if(ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
            )!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            )!= PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions()
                return
            }

            if(isLocationEnabled())
            {
                    fused.lastLocation.addOnCompleteListener(this) {task ->
                        val location: Location? =task.result

                        if(location==null) {

                            Toast.makeText(this, "null Received", Toast.LENGTH_SHORT).show()

                        }
                        else
                        {
                            Toast.makeText(this, "Get success", Toast.LENGTH_SHORT).show()
                            fetchCurrentLocationWeather(
                                location.latitude.toString(),
                                location.longitude.toString()

                            )



                        }
                    }
            }
            else
            {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            requestPermissions()
        }
    }

    private fun isLocationEnabled() :Boolean{
        val locationManager: LocationManager=getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),

            PERMISSION_REQUEST_ACCESS_LOCATION
        )


    }
    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
      //  const val API_KEY =
    }
    private fun checkPermission() : Boolean {

        if(ActivityCompat.checkSelfPermission(this,
            Manifest.permission.ACCESS_COARSE_LOCATION)
            ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
           Manifest.permission.ACCESS_FINE_LOCATION )== PackageManager.PERMISSION_GRANTED)
        {
            return  true
        }
        return  false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array< String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if(grantResults.isNotEmpty()&& grantResults[0]== PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(applicationContext,"granted", Toast.LENGTH_SHORT).show()
                getLocation()
            }
            else
            {
                Toast.makeText(applicationContext,"denied", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun fetchCurrentLocationWeather(latitude: String, longitude: String) {

        activityMainBinding.pbLoading.visibility = VISIBLE

        WeatherApiService.getWeatherApi()?.getCurrentWeatherData(latitude, longitude, API_KEY  )//API_KEY
            ?.enqueue(object :
                Callback<WeatherModel> {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onResponse(call: Call<WeatherModel>, response: Response<WeatherModel>) {

                    if (response.isSuccessful) {

                        gettData(response.body())
                    }
                }

                override fun onFailure(call: Call<WeatherModel>, t: Throwable) {
                    Toast.makeText(applicationContext, "Error!", Toast.LENGTH_SHORT).show()
                }
            })


    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun gettData(body: WeatherModel?) {
        activityMainBinding.mainContent.visibility = VISIBLE
        activityMainBinding.pbLoading.visibility = View.GONE

        activityMainBinding.temp.text = kelvinToCelsius( body!!.main.temp).toString() + "°C"
        activityMainBinding.city.text = body.name
        activityMainBinding.country.text = body.sys.country
        activityMainBinding.feelsLike.text = kelvinToCelsius(body.main.feelsLike).toString() +"°C"
        activityMainBinding.infoWeather.text = body.weather[0].description
        activityMainBinding.sunrise.text = getTime(body.sys.sunrise.toLong())
        activityMainBinding.sunset.text = getTime(body.sys.sunset.toLong())
        activityMainBinding.pressure.text = body.main.pressure.toString()
        activityMainBinding.wind.text = body.wind.speed.toString() + " m/s"
        activityMainBinding.edtCityName.setText(body.name)


        Glide.with(this)
            .load("http://openweathermap.org/img/wn/" + body.weather[0].icon + "@2x.png")
            .into(img_weather_icon)
    }


    }


   private fun getTime(timestamp1 : Long): String? {
        val times= SimpleDateFormat("k:mm", Locale.ENGLISH)
        val date = Date( timestamp1* 1000)
        return times.format(date)
    }

    private fun kelvinToCelsius(temp: Double): Int {
        var intTemp = temp
        intTemp = intTemp.minus(273)
        return intTemp.toBigDecimal().setScale(0, RoundingMode.UP).toInt()
}




