package com.example.bussro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class BusListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_list)

        initBusListRv()
    }

    private fun initBusListRv() {
        val rvAdapter = BusListAdapter(applicationContext, getTempBusList())
        findViewById<RecyclerView>(R.id.rv_bus_list).apply {
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