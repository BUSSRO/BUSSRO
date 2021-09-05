package com.example.bussro.feature.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bussro.R
import com.example.bussro.databinding.ActivityHistoryBinding
import com.example.bussro.model.db.entity.History
import com.example.bussro.util.CustomItemDecoration

/**
 * [HistoryActivity]
 * MainActivity 의 "히스토리" 클릭시 보이는 화면
 * 사용자가 검색했던 버스 정류장 이력을 확인할 수 있다.
 */

class HistoryActivity : AppCompatActivity() {
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initVar()
//        viewModel.deleteAll()
    }

    private fun initVar() {
        val rvAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            addItemDecoration(CustomItemDecoration(60))
        }

        viewModel.getAll().observe(this, Observer {
            Log.d("test", "onCreate: ${it}")
            rvAdapter.updateData(it)
        })
    }
}