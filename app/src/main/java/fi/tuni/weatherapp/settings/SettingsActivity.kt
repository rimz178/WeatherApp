package fi.tuni.weatherapp.settings

import android.R.layout.simple_list_item_1
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import fi.tuni.weatherapp.R
import fi.tuni.weatherapp.view.MainActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.settings_activity.*

class SettingsActivity : AppCompatActivity() {


    private lateinit var myPreference: MyPreference
    private lateinit var context: Context

    private val languageList = arrayOf("En", "Fi")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        context = this
        myPreference = MyPreference(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val lang = myPreference.getLoginCount()
         val index = languageList.indexOf(lang)

        // create spinner list
        spinner.adapter = ArrayAdapter(this, simple_list_item_1, languageList)

        //this if statement shows which language is selected in the spinner list
        if (index >= 0) {
            spinner.setSelection(index)
        }


        // when the user selects a language from the list, the application switches to the main view
        // and the language changes to the language selected by the user.
        button.setOnClickListener {
            myPreference.setLoginCount(languageList[spinner.selectedItemPosition])
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }
}




