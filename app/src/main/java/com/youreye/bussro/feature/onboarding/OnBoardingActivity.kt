package com.youreye.bussro.feature.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityOnBoardingBinding
import com.youreye.bussro.feature.clause.ClauseActivity

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
        initViewPager()
        setOnClickListener()
    }

    private fun initViewPager() {
        // ViewPager2 초기화
        val pagerAdapter = PagerFragmentStateAdapter(this)

        pagerAdapter.addFragment(FirstFragment())
        pagerAdapter.addFragment(SecondFragment())
        pagerAdapter.addFragment(ThirdFragment())

        binding.vpOnboarding.adapter = pagerAdapter
    }

    private fun setOnClickListener() {
        binding.txtOnboardingNext.setOnClickListener {
            if (binding.vpOnboarding.currentItem == 2) {
                startActivity(Intent(this, ClauseActivity::class.java))
            } else {
                binding.vpOnboarding.currentItem += 1
            }
        }
    }
}