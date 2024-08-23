package com.avi.gharkhojo.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Adapter.ImageAdapter
import com.avi.gharkhojo.R
import com.google.android.material.tabs.TabLayout


class TabLayoutFragment : Fragment(R.layout.fragment_tab_layout) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout

    private val livingRoomImages = listOf(
        R.drawable.home6,
        R.drawable.home8,
        R.drawable.home9,
        R.drawable.home3
    )

    private val officeImages = listOf(
        R.drawable.home1,
        R.drawable.home5
    )

    private val bedroomImages = listOf(
        R.drawable.home2,
        R.drawable.home4,
        R.drawable.home7
    )

    private val walkInRobeImages = listOf(
        R.drawable.home11
    )

    private enum class FilterMode {
        All,
        LivingRoom,
        Office,
        Bedroom,
        WalkInRobe
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        tabLayout = view.findViewById(R.id.tabLayout)

        val images = mapOf(
            FilterMode.LivingRoom to livingRoomImages,
            FilterMode.Office to officeImages,
            FilterMode.Bedroom to bedroomImages,
            FilterMode.WalkInRobe to walkInRobeImages
        )

        tabLayout.addTab(tabLayout.newTab().setText("All"))
        images.keys.forEach { mode ->
            tabLayout.addTab(tabLayout.newTab().setText(mode.name))
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val selectedMode = tab?.position?.let { FilterMode.values()[it] }
                updateRecyclerView(selectedMode)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Initial load
        updateRecyclerView(FilterMode.All)
    }

    private fun updateRecyclerView(filterMode: FilterMode?) {
        val selectedImages = when (filterMode) {
            FilterMode.All -> livingRoomImages + officeImages + bedroomImages + walkInRobeImages
            FilterMode.LivingRoom -> livingRoomImages
            FilterMode.Office -> officeImages
            FilterMode.Bedroom -> bedroomImages
            FilterMode.WalkInRobe -> walkInRobeImages
            else -> emptyList()
        }
        recyclerView.adapter = ImageAdapter(selectedImages)
    }
}
