package com.avi.gharkhojo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        saveLastUsedActivity()
    }

    private fun saveLastUsedActivity() {
        val sharedPref = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("lastUsedActivity", javaClass.name)
            apply()
        }
    }
}
