package com.youreye.bussro.feature.nearbybusstop

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.model.network.response.NearbyBusStopData
import com.youreye.bussro.databinding.RvNearbyBusStopItemBinding
import com.youreye.bussro.feature.buslist.BusListActivity
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * [NearbyBusStopAdapter]
 * NearbyBusStopActivity 의 RecyclerView Adapter
 * 주변 정류장 및 다른 정류장 찾기의 결과를 반영한다.
 *
 * @param application application from NearbyBusStopActivity
 */

class NearbyBusStopAdapter(
    private val application: Application
) : RecyclerView.Adapter<NearbyBusStopAdapter.NearbyBusStopViewHolder>() {
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
        /* ViewBinding data input */
        holder.binding.nearbyBusStop = data[position]

        /* 항목 click listener */
        holder.binding.apply {
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

