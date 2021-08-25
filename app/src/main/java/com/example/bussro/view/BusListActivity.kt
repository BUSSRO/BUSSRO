package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.adapter.BusListAdapter
import com.example.bussro.data.BusListRvData
import com.example.bussro.util.CustomItemDecoration
import com.example.bussro.R
import com.example.bussro.databinding.ActivityBusListBinding
import com.example.bussro.databinding.ActivityMainBinding

class BusListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBusListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_list)

        initBusListRv()
    }

    private fun initBusListRv() {
        val rvAdapter = BusListAdapter(applicationContext, getTempBusList())
        binding.rvBusList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                CustomItemDecoration(60)
            )
        }

        rvAdapter.notifyDataSetChanged()
    }

    // TODO 임시 데이터 제거
    private fun getTempBusList() : MutableList<BusListRvData> {
        val tempData = mutableListOf<BusListRvData>()

        for (i in 1..10) {
            tempData.add(BusListRvData())
        }

        return tempData
    }
}