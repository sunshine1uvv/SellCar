package com.example.sellcar.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.sellcar.R
import com.example.sellcar.data.Auto

class AutoAdapter(private val autoList: List<Auto>,
                  private val onLongClick: (String) -> Unit) :
    RecyclerView.Adapter<AutoAdapter.AutoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AutoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return AutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AutoViewHolder, position: Int) {
        val auto = autoList[position]
        holder.apply {
            textViewCarInfo.text = "${auto.brand} ${auto.model} ${auto.generation}"
            textViewPrice.text = "${auto.price} $"
            textViewDetails.text =
                "${auto.year}, ${auto.transmission}, ${auto.carBody}, ${auto.state}"
            val photoPagerAdapter = ImageAdapter(auto.imageUrls)
            holder.viewPager.adapter = photoPagerAdapter
            itemView.setOnLongClickListener {
                onLongClick(auto.id)
                true
            }
        }
    }

    override fun getItemCount(): Int {
        return autoList.size
    }

    class AutoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewCarInfo : TextView = itemView.findViewById(R.id.textViewCarInfo)
        val textViewPrice : TextView = itemView.findViewById(R.id.textViewPrice)
        val viewPager : ViewPager2 = itemView.findViewById(R.id.viewPager)
        val textViewDetails: TextView = itemView.findViewById(R.id.textViewDetails)
    }
}