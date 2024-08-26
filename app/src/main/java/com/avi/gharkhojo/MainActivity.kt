package com.avi.gharkhojo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.avi.gharkhojo.Chat.Chat_Activity
import com.avi.gharkhojo.databinding.ActivityMainBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: ChipNavigationBar

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        setupNavigation()
        setupTabBar()
        handleOnBackPressed()
    }

    fun hideBottomNavBar() {
        bottomNavigation.visibility = View.GONE
    }

    fun showBottomNavBar() {
        bottomNavigation.visibility = View.VISIBLE
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_nav_bar)
        bottomNavigation.setMenuResource(R.menu.nav_menu)
    }

    private fun setupTabBar() {
        bottomNavigation.setItemSelected(R.id.nav_home, true)
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id) {
                R.id.nav_home -> navController.navigate(R.id.home2)
                R.id.nav_chat -> startActivity(Intent(this, Chat_Activity::class.java))
                    //
                R.id.nav_bookMark -> {
                    navController.navigate(R.id.bookmarkFragment)
                }
                R.id.nav_profile -> navController.navigate(R.id.profile)
            }
        }
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (navController.currentDestination?.id != R.id.home2) {
                    navController.navigate(R.id.home2)
                    bottomNavigation.setItemSelected(R.id.nav_home, true)
                } else {
                    showExitConfirmationDialog()
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showExitConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_custom, null)
        val dialog = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        dialogView.findViewById<TextView>(R.id.dialogTitle).text = "Exit"
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = "Are you sure you want to exit the app?"

        dialogView.findViewById<Button>(R.id.dialogButtonYes).setOnClickListener {
            finishAffinity()
        }

        dialogView.findViewById<Button>(R.id.dialogButtonNo).setOnClickListener {
            dialog.dismiss()
        }
    }

}
