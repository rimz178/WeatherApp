package fi.tuni.weatherapp.settings

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME = "SharedPreferenceExample"
const val PREFERENCE_LANGUAGE = "Language"

class MyPreference(context : Context){


    private val preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    // this function sets the default language when you open the application for the first time
    fun getLoginCount() : String? {
        return preference.getString(PREFERENCE_LANGUAGE,"fi")
    }

    fun setLoginCount(Language:String){
        val editor = preference.edit()
        editor.putString(PREFERENCE_LANGUAGE,Language)
        editor.apply()
    }

}