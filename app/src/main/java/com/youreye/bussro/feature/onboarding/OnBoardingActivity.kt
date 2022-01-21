package com.youreye.bussro.feature.onboarding

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityOnBoardingBinding
import com.youreye.bussro.feature.clause.ClauseActivity
import com.youreye.bussro.util.BackPressDialog

/**
 * [OnBoardingActivity]
 * 앱을 처음 실행하는 사용자에게 SplashActivity 후 보여짐
 * 버스스로 앱을 소개한다.
 */


class OnBoardingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_on_boarding)
        // 상태바 색상 변경
        this.window.statusBarColor = resources.getColor(R.color.black)

        initViewPager()
        setOnClickListener()
    }

    private fun initViewPager() {
        // ViewPager2 초기화
        val pagerAdapter = PagerFragmentStateAdapter(this)

        pagerAdapter.addFragment(FirstFragment())
        pagerAdapter.addFragment(SecondFragment())
        pagerAdapter.addFragment(ThirdFragment())

        binding.vpOnBoarding.apply {
            adapter = pagerAdapter

            // ban overscroll
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            // overriding onPagedSelectedListener
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    if (position == 2) {
                        binding.txtOnBoardingNext.text = "약관동의 페이지로 이동"
                        binding.txtOnBoardingNext.setBackgroundColor(Color.parseColor("#FFCC00"))
                    } else {
                        binding.txtOnBoardingNext.text = "다음"
                        binding.txtOnBoardingNext.setBackgroundColor(Color.parseColor("#AAAAAA"))
                    }
                }
            })
        }

        // apply ViewPager2 indicator
        binding.wdiOnBoarding.setViewPager2(binding.vpOnBoarding)
    }

    private fun setOnClickListener() {
        binding.txtOnBoardingNext.setOnClickListener {
            if (binding.vpOnBoarding.currentItem == 2) {
                startActivity(Intent(this, ClauseActivity::class.java))
            } else {
                binding.vpOnBoarding.currentItem += 1
            }
        }
    }

    override fun onBackPressed() {
        val dialog = BackPressDialog()
        dialog.show(supportFragmentManager, "BackPressDialog")
    }
}