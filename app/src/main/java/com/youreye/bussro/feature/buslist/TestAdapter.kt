package com.youreye.bussro.feature.buslist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.youreye.bussro.R
import com.youreye.bussro.model.network.response.BusListData

class TestAdapter(val context: Context) : BaseAdapter() {
    private var data = listOf<BusListData.MsgBody.BusList>()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_bus_list_item, parent, false)



        return view
    }

    override fun getCount(): Int = data.size

    override fun getItem(position: Int): Any = data[position]

    override fun getItemId(position: Int): Long = position.toLong()
}
