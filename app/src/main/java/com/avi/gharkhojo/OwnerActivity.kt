package com.avi.gharkhojo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class OwnerActivity : BaseActivity() {
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: ChipNavigationBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_owner)


        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_owner) as? NavHostFragment
        navHostFragment?.let {
            navController = it.navController
        } ?: run {
            Log.e("OwnerActivity", "NavHostFragment not found")
            return
        }

        bottomNavigation = findViewById(R.id.bottom_nav_bar_owner)
        bottomNavigation.setMenuResource(R.menu.menu_owner) // Set the menu resource
        setUpTabBar()


        if (savedInstanceState == null) {
            navController.navigate(R.id.addFragment)
            bottomNavigation.setItemSelected(R.id.addFragment, true) // Set ChipNavigationBar item to AddFragment
        }

        handleOnBackPressed()
    }

    private fun setUpTabBar() {
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id) {
                R.id.addFragment -> {
                    navController.navigate(R.id.addFragment)
                }
                R.id.interestedFragment -> {
                    navController.navigate(R.id.interestedFragment)
                }
                R.id.uploadsFragment -> {
                    navController.navigate(R.id.uploadsFragment)
                }
                R.id.ownerProfileFragment -> {
                    navController.navigate(R.id.ownerProfileFragment)
                }
                else -> {
                    Log.e("OwnerActivity", "Unknown fragment id: $id")
                }
            }
        }
    }

    private fun handleOnBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val rootFragmentId = R.id.addFragment
                val currentDestination = navController.currentDestination?.id

                Log.d("HandleBackPress", "Current Destination: $currentDestination, Root Fragment: $rootFragmentId")

                if (currentDestination != rootFragmentId) {
                    if (!navController.popBackStack(rootFragmentId, false)) {

                        Log.d("HandleBackPress", "Navigating to Root Fragment")
                        navController.navigate(rootFragmentId)
                    }
                    bottomNavigation.setItemSelected(R.id.addFragment, true)
                    Log.d("HandleBackPress", "Set bottom navigation item selected")
                } else {
                    val intent = Intent(this@OwnerActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        })
    }

}
