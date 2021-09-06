package com.example.bussro.feature.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.bussro.BuildConfig
import com.example.bussro.R
import com.example.bussro.databinding.ActivityMainBinding

/**
 * [MainActivity]
 * SplashActivity 후 보여짐
 * 사용자는 원하는 버튼을 선택해서 원하는 기능으로 이동할 수 있다.
 */

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
        binding.model = model

        if (savedInstanceState == null) {
            removeAction()
        }
    }

    /* disable message "실행하려면 두 번 누르세요" */
    private fun removeAction() {
        val views = listOf(
            binding.imgMainSetting, binding.txtMainFirst, binding.txtMainSecond, binding.txtMainThird
        )

        for (view in views) {
            view.accessibilityDelegate = object: View.AccessibilityDelegate() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View?,
                    info: AccessibilityNodeInfo?
                ) {
                    super.onInitializeAccessibilityNodeInfo(host, info)
                    info?.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK)
                    info?.isClickable = false
                }
            }
        }
    }
}