package com.example.bussro.feature.buslist

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.model.network.response.BusListData
import com.example.bussro.R
import com.example.bussro.databinding.RvBusListItemBinding

/**
 * [BusListAdapter]
 * BusListActivity 의 RecyclerView 에 연결될 Adapter
 *
 * @param buses 사용자가 안내받기로 선택한 버스
 * @param activity BusListActivity
 */

class BusListAdapter(
    private val buses: MutableList<String>,
    private val activity: BusListActivity
) : RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {
    private var data = listOf<BusListData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val binding: RvBusListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_bus_list_item,
            parent,
            false
        )
        return BusListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        holder.binding.busList = data[position]

        holder.binding.apply {
            // 데이터 input
            busList = data[position]
            // click listener
            root.setOnClickListener {
                if (buses.contains(busList?.rtNm)) {  // 선택 취소
                    buses.remove(busList?.rtNm)

                    root.setBackgroundColor(Color.parseColor("#ECEBFF"))
                    txtBusListItemNumber.typeface = Typeface.DEFAULT
                    txtBusListItemInfo.typeface = Typeface.DEFAULT
                } else {  // 선택
                    buses.add(busList?.rtNm!!)

                    root.setBackgroundColor(Color.parseColor("#FFCC00"))
                    txtBusListItemNumber.typeface = Typeface.DEFAULT_BOLD
                    txtBusListItemInfo.typeface = Typeface.DEFAULT_BOLD
                }

                activity.setVisible()
            }
        }
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<BusListData>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class BusListViewHolder(@NonNull val binding: RvBusListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}


