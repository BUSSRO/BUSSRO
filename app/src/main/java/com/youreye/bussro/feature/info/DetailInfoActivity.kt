package com.youreye.bussro.feature.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityDetailInfoBinding
import com.youreye.bussro.feature.dialog.SuggestionsDialog

class DetailInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_info)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initClickListener()
        initVar()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.txtDetailInfoBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 문의하러가기 */
        binding.txtDetailInfoErrorSuggestions.setOnClickListener {
            SuggestionsDialog().show(supportFragmentManager, "SuggestionsDialog")
        }
    }

    private fun initVar() {
        /* 선택한 항목 안내 초기화 */
        val category = intent.getStringExtra("category") ?: ""
        binding.txtDetailInfoCategory.text = category
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}