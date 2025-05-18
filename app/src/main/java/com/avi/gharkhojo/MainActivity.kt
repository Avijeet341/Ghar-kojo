package com.avi.gharkhojo

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.avi.gharkhojo.Chat.Chat_Activity
import com.avi.gharkhojo.databinding.ActivityMainBinding
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.blurry.Blurry

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

    @SuppressLint("SetTextI18s")
    private fun showExitConfirmationDialog() {
        // Create a container for our dialog backdrop
        val backdropView = View(this).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor("#59000000"))  // Semi-transparent black
        }

        // Add backdrop to the screen
        val rootView = mainBinding.root as ViewGroup
        rootView.addView(backdropView)

        // Get the root view for blurring - cast to ViewGroup explicitly
        val blurTarget = window.decorView.findViewById<ViewGroup>(android.R.id.content)

        // Apply the blur effect
        Blurry.with(this)
            .radius(3)  // Blur intensity (you can adjust)
            .sampling(2)  // Performance/quality balance (1-8, higher is faster)
            .color(Color.parseColor("#15FFFFFF"))  // Light white tint
            .async()  // Run on background thread
            .animate(500)  // Animation duration
            .onto(blurTarget)

        // Create and show the dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_custom, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set background to transparent
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Set animation
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        // Set button click listeners
        dialogView.findViewById<TextView>(R.id.dialogButtonNo).apply {
            setOnClickListener {
                // Clean up blur and backdrop
                Blurry.delete(blurTarget)
                rootView.removeView(backdropView)
                dialog.dismiss()
            }
        }

        dialogView.findViewById<TextView>(R.id.dialogButtonYes).apply {
            setOnClickListener {
                // Clean up blur and backdrop
                Blurry.delete(blurTarget)
                rootView.removeView(backdropView)
                finishAffinity()
            }
        }

        // Clean up when dialog is dismissed
        dialog.setOnDismissListener {
            Blurry.delete(blurTarget)
            rootView.removeView(backdropView)
        }

        dialog.show()
    }
    override fun onResume() {
        super.onResume()
        if (bottomNavigation.getSelectedItemId() == R.id.nav_chat) {
            bottomNavigation.setItemSelected(R.id.nav_home, true)
        }
    }
}