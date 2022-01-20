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
import com.youreye.bussro.databinding.RvLoadingItemBinding
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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = listOf<BusStopData.MsgBody.BusStop>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvBusStopItemBinding.inflate(layoutInflater, parent, false)
                BusStopViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RvLoadingItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BusStopViewHolder) {
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
    }

    /* view type 지정 */
    override fun getItemViewType(position: Int): Int =
        if (data[position].arsId == "") VIEW_TYPE_LOADING else VIEW_TYPE_ITEM

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<BusStopData.MsgBody.BusStop>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* 데이터 추가 */
    fun addData(apiData: List<BusStopData.MsgBody.BusStop>) {
//        data.addAll(apiData)
        // progress bar 를 위한 데이터
//        data.add(BusStopData.MsgBody.BusStop(
//            "",
//            0.0,
//            ""
//        ))
    }

    /* 로딩바 제거 */
    fun deleteLoading() {
//        data.removeAt(data.lastIndex)
    }

    /* item ViewHolder */
    inner class BusStopViewHolder(@NonNull val binding: RvBusStopItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    /* loading ViewHolder */
    inner class LoadingViewHolder(@NonNull val binding: RvLoadingItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}

