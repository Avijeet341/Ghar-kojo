package com.avi.gharkhojo.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.avi.gharkhojo.Model.HousingType
import com.avi.gharkhojo.R
import com.google.common.collect.ImmutableList

class HousingTypeAdapter(
    private val housingTypes: List<HousingType>,
    private val onAddPropertyClick: () -> Unit,
    private val onItemClick: (MutableList<String>?) -> Unit
) : RecyclerView.Adapter<HousingTypeAdapter.ViewHolder>() {
    var housingTypeList:MutableList<String>? = arrayListOf()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageButton: ImageButton = view.findViewById(R.id.imageButton)
        val textView: TextView = view.findViewById(R.id.textView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tool_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val housingType = housingTypes[position]
        holder.imageButton.setImageResource(housingType.icon)
        holder.textView.text = housingType.name



        holder.imageButton.setOnClickListener {
           when{
               holder.textView.text == "Add Property" -> onAddPropertyClick()
               else ->{


                   housingType.clicked = !housingType.clicked
                   if(!housingType.clicked){
                       holder.textView.setTextAppearance(R.style.UnselectedTextFilter)
                       holder.imageButton.setImageResource(housingType.icon)


                           housingTypeList?.remove(holder.textView.text.toString())

                   }
                   else{
                       holder.textView.setTextAppearance(R.style.selectedTextFilter)
                       holder.imageButton.setImageResource(R.drawable.ic_tick)
                           housingTypeList?.add( holder.textView.text.toString())

                   }

                   onItemClick(housingTypeList)

               }

           }


        }
    }

    override fun getItemCount() = housingTypes.size
}
