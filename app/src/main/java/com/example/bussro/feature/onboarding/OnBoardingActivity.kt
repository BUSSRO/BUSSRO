package com.example.bussro.feature.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityHistoryBinding
import com.example.bussro.databinding.ActivityOnBoardingBinding
import com.example.bussro.feature.main.MainActivity

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
                Toast.makeText(this, "마지막", Toast.LENGTH_SHORT).show()
            } else {
                binding.vpOnboarding.currentItem += 1
            }
        }
    }
}