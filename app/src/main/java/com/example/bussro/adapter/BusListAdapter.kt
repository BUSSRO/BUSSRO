package com.example.bussro.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.data.BusListRvData
import com.example.bussro.R
import com.example.bussro.view.BusListActivity
import com.example.bussro.view.MainActivity

class BusListAdapter(
    private val context: Context,
    private val data: MutableList<BusListRvData>,
    private val busList: MutableList<String>,  // 선택된 버스 리스트
    private val activity: BusListActivity
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

            view.setOnClickListener {
                if (busList.contains(busNumber.text)) {  // 선택 취소
                    busList.remove(busNumber.text)

                    view.setBackgroundColor(Color.parseColor("#ECEBFF"))
                    busNumber.typeface = Typeface.DEFAULT
                    busInfo.typeface = Typeface.DEFAULT
                }
                else {  // 선택
                    busList.add(busNumber.text.toString())

                    view.setBackgroundColor(Color.parseColor("#FFCC00"))
                    busNumber.typeface = Typeface.DEFAULT_BOLD
                    busInfo.typeface = Typeface.DEFAULT_BOLD
                }

                activity.setVisible()

            }
        }
    }
}