package com.example.bussro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.R
import com.example.bussro.data.NearbyBusStopData
import com.example.bussro.view.buslist.BusListActivity

/**
 * [NearbyBusStopAdapter]
 * NearbyBusStopActivity 의 RecyclerView Adapter
 * 주변 정류장 및 다른 정류장 찾기의 결과를 반영한다.
 */

class NearbyBusStopAdapter(
    private val context: Context
) :RecyclerView.Adapter<NearbyBusStopAdapter.NearbyBusStopViewHolder>() {
    private var data = listOf<NearbyBusStopData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyBusStopViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_bus_list_item, parent, false)
        return NearbyBusStopViewHolder(view)
    }

    override fun onBindViewHolder(holder: NearbyBusStopViewHolder, position: Int) {
        // TODO: replace to Binding
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<NearbyBusStopData>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class NearbyBusStopViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private var stationNm = view.findViewById<TextView>(R.id.txt_bus_list_item_number)
        private var dist = view.findViewById<TextView>(R.id.txt_bus_list_item_info)

        @SuppressLint("SetTextI18n")
        fun bind(data: NearbyBusStopData) {
            stationNm.text = data.stationNm
            dist.text = data.dist.toString() + "km"

            view.setOnClickListener {
                val intent = Intent(view.context, BusListActivity::class.java)
                    .putExtra("station", data.stationNm)
                    .putExtra("arsId", data.arsId)
                view.context.startActivity(intent)
            }
        }
    }
}