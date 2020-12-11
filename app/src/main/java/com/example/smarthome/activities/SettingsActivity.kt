package com.example.smarthome.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.example.smarthome.R
import com.example.smarthome.entities.RoomEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.settings_activity.*
import java.util.*

class SettingsActivity:ToolbarHelper() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocate()
        setContentView(R.layout.settings_activity)

        val db = FirebaseFirestore.getInstance()

        language.setOnClickListener{
            showChangeLang()
        }

        delete_all_data.setOnClickListener {
            db.collection("rooms")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        var id = document.get("id").toString()
                        db.collection("rooms").document(id).delete()
                    }
                    Toast.makeText(this, R.string.app_resetted, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Log.w("FireStore", "Error getting documents: ", exception)
                }
        }

        db.collection("settings").document("ui_mode")
            .get()
            .addOnSuccessListener {document ->
                val mode = document.get("mode").toString()
                ui_mode.isChecked = mode != "light"
            }


        ui_mode.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
                db.collection("settings").document("ui_mode")
                    .update("mode", "dark")
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
                db.collection("settings").document("ui_mode")
                    .update("mode", "light")
            }
        }
    }

    private fun showChangeLang() {

        val listItmes = arrayOf("English", "Eesti", "Azerbaijani", "Ukrainian")

        val mBuilder = AlertDialog.Builder(this)
        mBuilder.setTitle("Choose Language")
        mBuilder.setSingleChoiceItems(listItmes, -1) { dialog, which ->
            if (which == 0) {
                setLocate("en")
                recreate()
            } else if (which == 1) {
                setLocate("et")
                recreate()
            } else if (which == 2) {
                setLocate("az")
                recreate()
            } else if (which == 3) {
                setLocate("uk")
                recreate()
            }
            dialog.dismiss()
        }
        val mDialog = mBuilder.create()
        mDialog.show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
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