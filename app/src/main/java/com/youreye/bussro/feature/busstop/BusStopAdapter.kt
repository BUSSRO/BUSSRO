package com.youreye.bussro.feature.busstop

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvBusStopItemBinding
import com.youreye.bussro.feature.buslist.BusListActivity
import com.youreye.bussro.model.network.response.BusStopData

/**
 * [BusStopAdapter]
 * NearbyBusStopActivity 의 RecyclerView Adapter
 * 주변 정류장 및 다른 정류장 찾기의 결과를 반영한다.
 *
 * @param application application from NearbyBusStopActivity
 */

class BusStopAdapter(
    private val application: Application
) : RecyclerView.Adapter<BusStopAdapter.BusStopViewHolder>() {
    private var data = listOf<BusStopData.MsgBody.BusStop>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopViewHolder {
        val binding: RvBusStopItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_bus_stop_item,
            parent,
            false
        )
        return BusStopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusStopViewHolder, position: Int) {
        /* ViewBinding data input */
        holder.binding.busStop = data[position]

        /* 항목 click listener */
        holder.binding.apply {
            root.setOnClickListener { view ->
                val intent = Intent(view.context, BusListActivity::class.java)
                    .putExtra("stationNm", busStop?.stationNm)
                    .putExtra("arsId", busStop?.arsId)
                view.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<BusStopData.MsgBody.BusStop>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class BusStopViewHolder(@NonNull val binding: RvBusStopItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}

