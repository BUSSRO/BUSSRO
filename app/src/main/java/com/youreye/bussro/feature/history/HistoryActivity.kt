package com.youreye.bussro.feature.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityHistoryBinding
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import com.youreye.bussro.util.BussroExceptionHandler
import com.youreye.bussro.util.logd
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
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
    private lateinit var category: Array<String>
    @Inject lateinit var historyRepository: HistoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BussroExceptionHandler.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initVar()
//        viewModel.deleteAll()
    }

    @SuppressLint("SetTextI18n")
    private fun initVar() {
        /* 뒤로가기 */
        binding.ibHistoryBack.setOnClickListener {
            finish()
        }

        initViewPagerWithTabLayout()
        viewModel.getAll().observe(this, Observer {
            /* 버스 이용 횟수 */
            val text = "이번 달 버스 이용은 ${it.size} 번 입니다."
            val builder = SpannableStringBuilder(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.yellow))
            val end = 12 + (it.size.toString().length) + 2
            builder.setSpan(colorSpan, 12, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.txtHistoryDescription.text = builder
        })
    }

    /* TabLayout + ViewPager2 */
    private fun initViewPagerWithTabLayout() {
        // FragmentStateAdapter 초기화
        val pAdapter = HistoryPagerFragmentStateAdapter(this)
            .apply {
                addFragment(HistoryFragment())
                addFragment(BookmarkFragment())
            }

        // ViewPager2 의 Adapter 설정
        val vPager = binding.vpHistory.apply {
            adapter = pAdapter
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Log.d("HistoryActivity", "Page: ${position + 1}")
                }
            })
        }

        // TabLayout 과 ViewPager2 연결
        category = resources.getStringArray(R.array.history_category)

        TabLayoutMediator(binding.tlHistory, vPager) { tab, position ->
            tab.text = category[position]
        }.attach()
    }

    /* 30일 이전의 날짜 얻기 */
    private fun getDate(): String {
        val cal = Calendar.getInstance()
        cal.time = Date()
        cal.add(Calendar.MONTH, -1)

        val dateFormat = SimpleDateFormat("yy.MM.dd", Locale.getDefault())
        return dateFormat.format(cal.time)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}