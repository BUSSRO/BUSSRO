package com.example.bussro.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.data.BusListRvData
import com.example.bussro.R

class BusListAdapter(
    private val context: Context,
    private val data: MutableList<BusListRvData>
) : RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_bus_list_item, parent, false)
        return BusListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    /* ViewHolder */
    inner class BusListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val busNumber: TextView = view.findViewById(R.id.txt_bus_list_item_number)
        private val busInfo: TextView = view.findViewById(R.id.txt_bus_list_item_info)

        fun bind(data: BusListRvData) {
            busNumber.text = data.number
            busInfo.text = data.info
        }
    }
}