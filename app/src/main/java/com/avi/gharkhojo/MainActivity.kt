package com.avi.gharkhojo

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.avi.gharkhojo.Model.SharedViewModel
import com.avi.gharkhojo.databinding.ActivityMainBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Log.d("MainActivity", "User data updated:")

        // Log user data changes in SharedViewModel


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_nav_bar)

        bottomNavigation.setMenuResource(R.menu.nav_menu) // Set the menu resource
        setUpTabBar()
        onBackPressedAvi()
    }

    private fun onBackPressedAvi() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id != R.id.home2) {
                    navController.navigate(R.id.home2)
                    bottomNavigation.setItemSelected(R.id.nav_home, true)
                } else {
                    showExitConfirmationDialog()
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun setUpTabBar() {
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id) {
                R.id.nav_home -> {
                    navController.navigate(R.id.home2)
                }
                R.id.nav_search -> {
                    navController.navigate(R.id.search)
                }
                R.id.nav_cart -> {
                    mainBinding.apply {
                        // textMain.text="Fire."
                        bottomNavBar.showBadge(R.id.nav_profile)
                    }
                    navController.navigate(R.id.cart)
                }
                R.id.nav_profile -> {
                    mainBinding.apply {
                        // textMain.text="Profile."
                        bottomNavBar.dismissBadge(R.id.nav_profile)
                    }
                    navController.navigate(R.id.profile)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showExitConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)

        val dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        val title = dialogView.findViewById<TextView>(R.id.dialogTitle)
        val message = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val buttonYes = dialogView.findViewById<Button>(R.id.dialogButtonYes)
        val buttonNo = dialogView.findViewById<Button>(R.id.dialogButtonNo)

        title.text = "Exit"
        message.text = "Are you sure you want to exit the app?"

        buttonYes.setOnClickListener {
            finishAffinity()
        }

        buttonNo.setOnClickListener {
            dialog.dismiss()
        }
    }
}
