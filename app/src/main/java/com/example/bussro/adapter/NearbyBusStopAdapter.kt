package com.example.bussro.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.R
import com.example.bussro.data.NearbyBusStopData
import com.example.bussro.databinding.RvNearbyBusStopItemBinding
import com.example.bussro.view.buslist.BusListActivity

/**
 * [NearbyBusStopAdapter]
 * NearbyBusStopActivity 의 RecyclerView Adapter
 * 주변 정류장 및 다른 정류장 찾기의 결과를 반영한다.
 */

class NearbyBusStopAdapter : RecyclerView.Adapter<NearbyBusStopAdapter.NearbyBusStopViewHolder>() {
    private var data = listOf<NearbyBusStopData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearbyBusStopViewHolder {
        val binding: RvNearbyBusStopItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_nearby_bus_stop_item,
            parent,
            false
        )
        return NearbyBusStopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NearbyBusStopViewHolder, position: Int) {
        holder.binding.apply {
            // 데이터 input
            nearbyBusStop = data[position]
            // click listener
            root.setOnClickListener { view ->
                val intent = Intent(view.context, BusListActivity::class.java)
                    .putExtra("stationNm", nearbyBusStop?.stationNm)
                    .putExtra("arsId", nearbyBusStop?.arsId)
                view.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<NearbyBusStopData>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class NearbyBusStopViewHolder(@NonNull val binding: RvNearbyBusStopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}

