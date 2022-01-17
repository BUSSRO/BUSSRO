package com.youreye.bussro.feature.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.CheckBox
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityHistoryBinding
import com.youreye.bussro.model.repository.HistoryRepository
import com.youreye.bussro.util.CustomItemDecoration
import com.youreye.bussro.util.logd
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * [HistoryActivity]
 * MainActivity 의 "히스토리" 클릭시 보이는 화면
 * 사용자가 검색했던 버스 정류장 이력을 확인할 수 있다.
 */

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var binding: ActivityHistoryBinding
    @Inject lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        initVar()
//        viewModel.deleteAll()
    }

    @SuppressLint("SetTextI18n")
    private fun initVar() {
        /* RecyclerView */
        val rvAdapter = HistoryAdapter(supportFragmentManager, historyRepository, application)
        binding.rvHistory.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            addItemDecoration(CustomItemDecoration(40))
        }

        viewModel.getAll().observe(this, Observer {
//            logd("onCreate: ${it}")
            rvAdapter.updateData(it)

            /* 버스 이용 횟수 */
            val text = "이번 달 버스 이용은 ${it.size} 번 입니다."
            val builder = SpannableStringBuilder(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.yellow))
            val end = 12 + (it.size / 10) + 3  // 시작점 + 숫자 자릿수
            builder.setSpan(colorSpan, 12, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.txtHistoryDescription.text = builder
        })
    }
}