package com.avi.gharkhojo

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

        // Set status bar color to black
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, android.R.color.black)

        // Optional: If you want white icons on the black status bar
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_owner) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_nav_bar_owner)

        bottomNavigation.setMenuResource(R.menu.menu_owner) // Set the menu resource
        setUpTabBar()
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
            }
        }
    }
}