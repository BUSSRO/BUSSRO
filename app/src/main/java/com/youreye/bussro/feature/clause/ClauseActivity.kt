package com.youreye.bussro.feature.clause

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityClauseBinding
import com.youreye.bussro.feature.main.MainActivity
import com.youreye.bussro.util.User

class ClauseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clause)

        initVar()
    }

    private fun initVar() {
        /* 뒤로가기 */
        binding.ibClauseBack.setOnClickListener {
            finish()
        }

        /* 전체 동의하기 */
        binding.cbClauseSelectAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.cbClauseFirst.isChecked = true
                binding.cbClauseSecond.isChecked = true
                binding.cbClauseThird.isChecked = true
            } else {
                binding.cbClauseFirst.isChecked = false
                binding.cbClauseSecond.isChecked = false
                binding.cbClauseThird.isChecked = false
            }
        }

        /* 앱 시작하기 */
        binding.txtClauseStart.setOnClickListener {
            if (binding.cbClauseFirst.isChecked && binding.cbClauseSecond.isChecked) {
                /* 앱 최초 실행여부 저장 */
                User.setFirst(this, false)

                startActivity(Intent(this, MainActivity::class.java))
                ActivityCompat.finishAffinity(this)
            } else {
                Toast.makeText(this, "필수 약관에 동의해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

