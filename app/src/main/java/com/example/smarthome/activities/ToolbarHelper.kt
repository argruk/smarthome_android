package com.example.smarthome.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.smarthome.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

abstract class ToolbarHelper: AppCompatActivity() {

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val db = FirebaseFirestore.getInstance()
        loadLocate()
        menuInflater.inflate(R.menu.extra_menu, menu)
        db.collection("settings").document("ui_mode")
            .get()
            .addOnSuccessListener {document ->
                val mode = document.get("mode").toString()
                if(mode == "light"){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        return super.onCreateOptionsMenu(menu);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.add_room -> {
                val intent = Intent(this, CreateRoomActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            android.R.id.home -> {
                finish()
                return true
            }

        }
        return true
    }

    private fun setLocate(Lang: String) {

        val locale = Locale(Lang)

        Locale.setDefault(locale)

        val config = Configuration()

        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)

        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", Lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        language?.let { setLocate(it) }
        Log.i("Language", "${language}")
    }
}