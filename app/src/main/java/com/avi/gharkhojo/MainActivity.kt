package com.avi.gharkhojo

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.avi.gharkhojo.Chat.Chat_Activity
import com.avi.gharkhojo.Model.SharedViewModel
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.Model.UserSignupLoginManager
import com.avi.gharkhojo.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.storage
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: ChipNavigationBar
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storageRef:StorageReference by lazy { Firebase.storage.reference.child("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}") }


    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        Log.d("MainActivity", "User data updated:")





        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavigation = findViewById(R.id.bottom_nav_bar)

        bottomNavigation.setMenuResource(R.menu.nav_menu) // Set the menu resource
        setUpTabBar()
        onBackPressedAvi()

        UserSignupLoginManager.getInstance(this).setUp()





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
//                        bottomNavBar.showBadge(R.id.nav_profile)
                        startActivity(Intent(this@MainActivity, Chat_Activity::class.java))
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
        dialog.setCancelable(false)
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
