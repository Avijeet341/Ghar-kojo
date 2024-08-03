package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.R

class HousingTypeAdapter(private val housingTypes: List<HousingType>) :
    RecyclerView.Adapter<HousingTypeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        val textView: TextView = view.findViewById(R.id.textView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tool_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val housingType = housingTypes[position]
        holder.imageButton.setImageResource(housingType.icon)
        holder.textView.text = housingType.name
    }

    override fun getItemCount() = housingTypes.size
}