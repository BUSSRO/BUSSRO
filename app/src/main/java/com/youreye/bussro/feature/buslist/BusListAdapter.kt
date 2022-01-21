package com.youreye.bussro.feature.buslist

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.model.network.response.BusListData
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvBusListItemBinding
import com.youreye.bussro.util.BoardingDialog

/**
 * [BusListAdapter]
 * BusListActivity 의 RecyclerView 에 연결될 Adapter
 *
 * @param activity BusListActivity
 */

class BusListAdapter(
    private val activity: BusListActivity,
    private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {
    private var data = listOf<BusListData.MsgBody.BusList>()

    private var lastSelectedItem: View? = null
    private var lastSelectedPosition: Int = -1

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
        /* ViewBinding 에 data insert */
        holder.binding.busList = data[position]

        /* 항목 click listener */
        // CHECK: 임시
        holder.binding.root.setOnClickListener {
            val dialog = BoardingDialog(
                data[position].rtNm,
                data[position].stNm,
                data[position].arsId
            )

            dialog.show(supportFragmentManager, "FromBusListActivity")
        }

        /*
        /* 항목 click listener */
        holder.binding.root.setOnClickListener {
            /*
             * 다른것 선택시)
             * 이전에 선택되었던 아이템 해제 및 새로운 아이템 등록
             *
             * 같은것 선택시)
             * 이전에 선택되었던 아이템 해제
             */

            // 이전에 선택되었던 아이템 해제
            lastSelectedItem?.setBackgroundColor(Color.parseColor("#ECEBFF"))
            lastSelectedItem?.findViewById<TextView>(R.id.txt_bus_list_item_number)?.typeface = Typeface.DEFAULT
            lastSelectedItem?.findViewById<TextView>(R.id.txt_bus_list_item_info)?.typeface = Typeface.DEFAULT

            // 새로운 아이템 등록
            if (lastSelectedPosition != position) {
                holder.binding.root.setBackgroundColor(Color.parseColor("#FFCC00"))
                holder.binding.txtBusListItemNumber.typeface = Typeface.DEFAULT_BOLD
                holder.binding.txtBusListItemInfo.typeface = Typeface.DEFAULT_BOLD

                activity.rtNm = data[position].rtNm!!
                lastSelectedItem = holder.binding.root
                lastSelectedPosition = position
            } else {
                activity.rtNm = ""
                lastSelectedItem = null
                lastSelectedPosition = -1
            }

            activity.setVisible()
        }
         */
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<BusListData.MsgBody.BusList>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class BusListViewHolder(@NonNull val binding: RvBusListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}


