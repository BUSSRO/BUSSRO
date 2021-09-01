package com.example.bussro.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.data.BusListRvData
import com.example.bussro.R
import com.example.bussro.view.BusListActivity

/**
 * [BusListAdapter]
 * BusListActivity 의 RecyclerView 에 연결될 Adapter
 *
 * @param context
 * @param data 사용자가 위치한 버스 정류장을 지나는 버스들의 정보
 * @param busList 사용자가 안내받기로 선택한 버스
 * @param activity BusListActivity
 */

class BusListAdapter(
    private val context: Context,
    private val data: MutableList<BusListRvData>,
    private val busList: MutableList<String>,
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