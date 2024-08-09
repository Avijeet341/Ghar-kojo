package com.avi.gharkhojo.Fragments.OwnerFragments

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class ClearDataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sharedPref = applicationContext.getSharedPreferences("OwnerData", Context.MODE_PRIVATE) ?: return Result.failure()

        // Check if there's any data to delete
        val hasData = sharedPref.contains("ownerName") || sharedPref.contains("email") ||
                sharedPref.contains("tenantServed") || sharedPref.contains("propertyType") ||
                sharedPref.contains("preferredTenants") || sharedPref.contains("phoneNumber")

        if (hasData) {
            // If data exists, delete it
            with(sharedPref.edit()) {
                remove("ownerName")
                remove("email")
                remove("tenantServed")
                remove("propertyType")
                remove("preferredTenants")
                remove("phoneNumber")
                remove("last_updated")
                apply()
            }
        }

        return Result.success()
    }
}
