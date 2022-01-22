package com.youreye.bussro.feature.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityMainBinding
import com.youreye.bussro.feature.dialog.BackPressDialog
import com.youreye.bussro.util.ErrorHandlerManager
import java.lang.RuntimeException

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
        ErrorHandlerManager.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.model = model

        // 에러 테스트 버튼 click listener
        binding.btnErrorTest.setOnClickListener {
            throw RuntimeException("BussroExceptionHandler 테스트용 에러")
        }

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

    override fun onBackPressed() {

        val dialog = BackPressDialog()
        dialog.show(supportFragmentManager, "BackPressDialog")
    }
}