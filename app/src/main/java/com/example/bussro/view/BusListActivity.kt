package com.example.bussro.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
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
    private val busList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bus_list)

        // TODO TTS 안내
        initBusListRv()
        setOnClickListener()
    }

    private fun initBusListRv() {
        val rvAdapter = BusListAdapter(applicationContext, getTempBusList(), busList, this)
        binding.rvBusList.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                CustomItemDecoration(60)
            )
        }

        rvAdapter.notifyDataSetChanged()
    }

    private fun setOnClickListener() {
        binding.txtBusListStart.setOnClickListener {
            val intent = Intent(this, BusInfoActivity::class.java)
                .putExtras(bundleOf("busList" to busList))
            startActivity(intent)
        }
    }

    // TODO 임시 데이터 제거
    private fun getTempBusList() : MutableList<BusListRvData> {
        val tempData = mutableListOf<BusListRvData>()

        for (i in 1..10) {
            tempData.add(BusListRvData(i.toString()))
        }

        return tempData
    }

    fun setVisible() {
        if (busList.size > 0) {
            binding.txtBusListStart.visibility = View.VISIBLE
        } else {
            binding.txtBusListStart.visibility = View.INVISIBLE
        }
    }
}