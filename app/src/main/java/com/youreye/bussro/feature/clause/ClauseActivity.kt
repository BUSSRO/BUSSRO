package com.youreye.bussro.feature.clause

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityClauseBinding
import com.youreye.bussro.feature.main.MainActivity
import com.youreye.bussro.util.SharedPrefManager

class ClauseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clause)

        initVar()
    }

    private fun initVar() {
        /* CheckBox listener */
        binding.cbClauseFirst.setOnClickListener { onCheckChanged(binding.cbClauseFirst) }
        binding.cbClauseSecond.setOnClickListener { onCheckChanged(binding.cbClauseSecond) }
        binding.cbClauseThird.setOnClickListener { onCheckChanged(binding.cbClauseThird) }
        binding.cbClauseSelectAll.setOnClickListener { onCheckChanged(binding.cbClauseSelectAll) }

        /* 텍스트 색상 커스텀 */
        changeTextColor(binding.cbClauseFirst, 0, 4, resources.getColor(R.color.yellow))
        changeTextColor(binding.cbClauseSecond, 0, 4, resources.getColor(R.color.yellow))
        changeTextColor(binding.cbClauseDesc2, 0, 11, resources.getColor(R.color.orange))

        /* 뒤로가기 */
        binding.ibClauseBack.setOnClickListener {
            finish()
        }

        /* 앱 시작하기 */
        binding.txtClauseStart.setOnClickListener {
            if (binding.cbClauseFirst.isChecked && binding.cbClauseSecond.isChecked) {
                /* 앱 최초 실행여부 저장 */
                SharedPrefManager.setFirst(this, false)

                startActivity(Intent(this, MainActivity::class.java))
                ActivityCompat.finishAffinity(this)
            } else {
                Toast.makeText(this, "필수 약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /* 텍스트 색상 변경 */
    private fun changeTextColor(cb: CheckBox, start: Int, end: Int, color: Int) {
        val text = cb.text.toString()
        val builder = SpannableStringBuilder(text)
        val colorSpan = ForegroundColorSpan(color)
        builder.setSpan(colorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        cb.text = builder
    }

    /* 이용약관 동의 여부에 따라 뷰 상태 변화 */
    private fun onCheckChanged(compoundButton: CompoundButton) {
        // 전체 동의
        when (compoundButton.id) {
            R.id.cb_clause_select_all -> {
                if (binding.cbClauseSelectAll.isChecked) {
                    binding.cbClauseFirst.isChecked = true
                    binding.cbClauseSecond.isChecked = true
                    binding.cbClauseThird.isChecked = true
                } else {
                    binding.cbClauseFirst.isChecked = false
                    binding.cbClauseSecond.isChecked = false
                    binding.cbClauseThird.isChecked = false
                }
            }
            else -> {
                binding.cbClauseSelectAll.isChecked = (
                        binding.cbClauseFirst.isChecked
                        && binding.cbClauseSecond.isChecked
                        && binding.cbClauseThird.isChecked
                )
            }
        }

        // 앱 시작하기
        if (binding.cbClauseFirst.isChecked && binding.cbClauseSecond.isChecked) {
            binding.txtClauseStart.setBackgroundColor(resources.getColor(R.color.yellow))
        } else {
            binding.txtClauseStart.setBackgroundColor(resources.getColor(R.color.light_gray))
        }
    }
}