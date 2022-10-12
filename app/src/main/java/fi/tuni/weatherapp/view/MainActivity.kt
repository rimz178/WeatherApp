package fi.tuni.weatherapp.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import fi.tuni.weatherapp.R
import fi.tuni.weatherapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.text.Editable as Editable1


class MainActivity : AppCompatActivity() {


    private  lateinit var  fused : FusedLocationProviderClient

    private lateinit var viewmodel: MainViewModel
    private lateinit var GET: SharedPreferences
    private lateinit var SET: SharedPreferences.Editor
    private val df = DecimalFormat("#")



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GET = getSharedPreferences(packageName, MODE_PRIVATE)
        SET = GET.edit()
        viewmodel = ViewModelProviders.of(this)[MainViewModel::class.java]

        var cName = GET.getString("cityName","")?.toLowerCase()
        edt_city_name.setText(cName)
        viewmodel.refreshData(cName!!)

        fused = LocationServices.getFusedLocationProviderClient(this)
2


        getLiveData()
        getCurrentLocation()

        swipe_refresh_layout.setOnRefreshListener {
            mainContent.visibility = View.GONE
            tv_error.visibility = View.GONE
            pb_loading.visibility = View.GONE

            val cityName = GET.getString("cityName", cName)?.toLowerCase()
            edt_city_name.setText(cityName)
            viewmodel.refreshData(cityName!!)
            swipe_refresh_layout.isRefreshing = false
        }

        img_search_city.setOnClickListener {
            val cityName = edt_city_name.text.toString()
            SET.putString("cityName", cityName)
            SET.apply()
            viewmodel.refreshData(cityName)
            getLiveData()

        }

    }


    @SuppressLint("MissingPermission","SetTextI18n")
    private  fun getCurrentLocation() {

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
                            city.text =  getCityName(location.latitude,location.longitude)
                            country.text =  getCountryName(location.latitude,location.longitude)


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
        val locationManager: LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION,

        )
    }


    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 150

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
                getCurrentLocation()


            }
            else
            {
                Toast.makeText(applicationContext,"denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getCityName(lat: Double, long: Double): String {
        var cityname = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var adress = geoCoder.getFromLocation(lat,long,3)

        cityname = adress[0].locality
       Log.d("Debug:", "Your City: $cityname")
        return cityname
    }
    private fun getCountryName(lat: Double,long: Double):String{
         var countryName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var adress = geoCoder.getFromLocation(lat,long,3)
        countryName = adress[0].countryName
        return countryName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private fun getLiveData() {

        viewmodel.weather_data.observe(this) { data ->
            data?.let {
                mainContent.visibility = View.VISIBLE
                pb_loading.visibility = View.GONE

                country.text = data.sys.country
                city.text = data.name

                temp.text = df.format(data.main.temp).toString() + "°C"
                info_weather.text = data.weather[0].description
                sunrise.text = getTime(data.sys.sunrise.toLong())
                sunset.text = getTime(data.sys.sunset.toLong())

                feels_like.text = df.format(data.main.feelsLike).toString() + " °C"
                humidity.text = data.main.humidity.toString() + "%"

                wind.text = data.wind.speed.toString() + "m/s"
                pressure.text = data.main.pressure.toString() + "hPa"



                Glide.with(this)
                    .load("http://openweathermap.org/img/wn/" + data.weather[0].icon + "@2x.png")
                    .into(img_weather_icon)

            }
        }
        viewmodel.weather_error.observe(this) { error ->
            error?.let {
                if (it) {
                    tv_error.visibility = View.VISIBLE
                    pb_loading.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                } else {
                    tv_error.visibility = View.GONE

                }
            }
        }
        viewmodel.weather_load.observe(this) { loading ->
            loading?.let {
                if (it) {
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    mainContent.visibility = View.GONE
                } else {
                    pb_loading.visibility = View.GONE
                }
            }
        }

    }

   private fun getTime(timestamp1 : Long): String? {
        val times= SimpleDateFormat("k:mm", Locale.ENGLISH)
        val date = Date( timestamp1* 1000)
        return times.format(date)
    }



}

