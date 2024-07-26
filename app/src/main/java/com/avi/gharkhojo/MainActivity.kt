package com.avi.gharkhojo

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.avi.gharkhojo.Chat.Chat_Activity
import com.avi.gharkhojo.Model.UserData
import com.avi.gharkhojo.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var bottomNavigation: ChipNavigationBar
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val storageRef: StorageReference by lazy {
        FirebaseStorage.getInstance().reference.child("profile_pictures/${FirebaseAuth.getInstance().currentUser?.uid}")
    }

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

        updateUserData()
    }

    private fun updateUserData() {
        UserData.email = FirebaseAuth.getInstance().currentUser?.email
        UserData.profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
        UserData.username = FirebaseAuth.getInstance().currentUser?.displayName
        UserData.uid = FirebaseAuth.getInstance().currentUser?.uid

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = storageRef.downloadUrl.await()
                withContext(Dispatchers.Main) {
                    UserData.profilePictureUrl = url.toString()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    UserData.profilePictureUrl = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()
                }
            }

            try {
                val document = firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "").get().await()
                withContext(Dispatchers.Main) {
                    if (document != null) {
                        UserData.username = document.getString("name") ?: UserData.username
                        UserData.address = document.getString("address") ?: getString(R.string.default_address)
                        UserData.phn_no = document.getString("phone") ?: getString(R.string.default_phone)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    UserData.address = getString(R.string.default_address)
                    UserData.phn_no = getString(R.string.default_phone)
                }
            }
        }
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
                R.id.nav_home -> navController.navigate(R.id.home2)
                R.id.nav_search -> navController.navigate(R.id.search)
                R.id.nav_cart -> {
                    startActivity(Intent(this@MainActivity, Chat_Activity::class.java))
                    navController.navigate(R.id.cart)
                }
                R.id.nav_profile -> {
                    bottomNavigation.dismissBadge(R.id.nav_profile)
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

        buttonYes.setOnClickListener { finishAffinity() }
        buttonNo.setOnClickListener { dialog.dismiss() }
    }
}
