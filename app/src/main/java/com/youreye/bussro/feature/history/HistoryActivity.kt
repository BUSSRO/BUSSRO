package com.youreye.bussro.feature.history

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
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
    private lateinit var pagerAdapter: HistoryPagerAdapter
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

        /* RecyclerView */
//        val rvAdapter = HistoryAdapter(supportFragmentManager, application, historyRepository)
//        binding.rvHistory.apply {
//            adapter = rvAdapter
//            layoutManager = LinearLayoutManager(this@HistoryActivity)
////            addItemDecoration(CustomItemDecoration(40))
//        }

        viewModel.getAll().observe(this, Observer {
//            rvAdapter.updateData(it)
            pagerAdapter.updateData(it)

            /* 버스 이용 횟수 */
            val text = "이번 달 버스 이용은 ${it.size} 번 입니다."
            val builder = SpannableStringBuilder(text)
            val colorSpan = ForegroundColorSpan(resources.getColor(R.color.yellow))
            val end = 12 + (it.size.toString().length) + 2
            builder.setSpan(colorSpan, 12, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            binding.txtHistoryDescription.text = builder

            /* 버스 이용 횟수가 없는경우 띄워줄 이미지 + 텍스트 */
            if (it.isEmpty()) {
                binding.ivHistoryOff.visibility = View.VISIBLE
                binding.txtHistoryOffDesc.visibility = View.VISIBLE
            } else {
                binding.ivHistoryOff.visibility = View.GONE
                binding.txtHistoryOffDesc.visibility = View.GONE
            }
        })
    }

    private fun initViewPagerWithTabLayout() {
        category = resources.getStringArray(R.array.history_category)

        val viewPager = binding.vpHistory
        pagerAdapter = HistoryPagerAdapter(supportFragmentManager, application, historyRepository)

        viewPager.adapter = pagerAdapter
        viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        initTabLayout(viewPager)
    }

    /* TabLayout 초기화 */
    private fun initTabLayout(viewPager: ViewPager2) {
        val tab = binding.tlHistory

        // 텍스트 지정
        TabLayoutMediator(tab, viewPager) { tab, position ->
            tab.text = category[position]
        }.attach()

        // click listener
        tab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                logd("선택된 탭 : ${tab?.text.toString()}")

                // TODO: 선택된 탭에 따라 히스토리와 즐겨찾기 보여주기
                if (tab?.position == 0) {  // 히스토리

                } else {  // 즐겨찾기

                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) { }

            override fun onTabReselected(tab: TabLayout.Tab?) { }

        })
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