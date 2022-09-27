package fi.tuni.weatherapp.view

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import fi.tuni.weatherapp.R
import fi.tuni.weatherapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(){

   private lateinit var fusedlocations: FusedLocationProviderClient
   private lateinit var viewmodel: MainViewModel
   private lateinit var GET: SharedPreferences
   private lateinit var SET: SharedPreferences.Editor
   private var simpleDateFormat =  SimpleDateFormat(" k:mm", Locale.ENGLISH)

   private val df = DecimalFormat("#")


   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)
       GET = getSharedPreferences(packageName, MODE_PRIVATE)
       SET = GET.edit()
       viewmodel = ViewModelProviders.of(this)[MainViewModel::class.java]



       var cName = GET.getString("cityName", "Tampere")?.toLowerCase()
       edt_city_name.setText(cName)
       viewmodel.refreshData(cName!!)

    /*   val locationPermissionRequest = registerForActivityResult(
           ActivityResultContracts.RequestMultiplePermissions()
       ) { permissions ->
           when {
               permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                   Toast.makeText(applicationContext,"Sijainti päällä", Toast.LENGTH_LONG).show()
               }
               permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

               } else -> {
               Toast.makeText(applicationContext,"Sijainti ei ole päällä", Toast.LENGTH_LONG).show()
           }
           }


       locationPermissionRequest.launch(arrayOf(
           Manifest.permission.ACCESS_FINE_LOCATION,
           Manifest.permission.ACCESS_COARSE_LOCATION))
}*/
       getLiveData()

       swipe_refresh_layout.setOnRefreshListener {
           mainContent.visibility = View.GONE
           tv_error.visibility = View.GONE
           pb_loading.visibility = View.GONE

           var cityName = GET.getString("cityName", cName)?.toLowerCase()
           edt_city_name.setText(cityName)
           viewmodel.refreshData(cityName!!)
           swipe_refresh_layout.isRefreshing = false
       }

       img_search_city.setOnClickListener {
           val cityName = edt_city_name.text.toString()
           SET.putString("cityName", cityName)
           SET.apply()
           viewmodel.refreshData(cityName!!)
           getLiveData()

       }

   }



    @SuppressLint("SetTextI18n")
   private fun getLiveData() {

       viewmodel.weather_data.observe(this, Observer { data ->
           data?.let {
               mainContent.visibility = View.VISIBLE
               pb_loading.visibility = View.GONE

               country.text = data.sys.country
               city.text = data.name

               temp.text = df.format(data.main.temp).toString() +"°C"
               info_weather.text =  data.weather[0].description

               sunset.text  = simpleDateFormat.format( data.sys.sunset*1000).toString()
               sunrise.text = simpleDateFormat.format( data.sys.sunrise*1000).toString()

               feels_like.text = df.format(data.main.feelsLike).toString() + " °C"
               humidity.text =  data.main.humidity.toString() +"%"

               wind.text =  data.wind.speed.toString() +"m/s"
               pressure.text = data.main.pressure.toString() + "hPa"



               Glide.with(this)
                   .load("http://openweathermap.org/img/wn/" + data.weather[0].icon + "@2x.png")
                   .into(img_weather_icon)

           }
       })
        viewmodel.weather_error.observe(this, Observer { error ->
            error?.let {
                if (it) {
                    tv_error.visibility = View.VISIBLE
                    pb_loading.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                } else {
                    tv_error.visibility = View.GONE

                }
            }
        })
        viewmodel.weather_load.observe(this, Observer { loading ->
            loading?.let {
                if (it) {
                    pb_loading.visibility = View.VISIBLE
                    tv_error.visibility = View.GONE
                    mainContent.visibility = View.GONE
                } else {
                    pb_loading.visibility = View.GONE
                }
            }
        })

    }

}


