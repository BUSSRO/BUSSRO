package com.example.bussro.feature.clause

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityClauseBinding
import com.example.bussro.feature.main.MainActivity

class ClauseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityClauseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_clause)

        // 뒤로가기
        binding.ibClauseBack.setOnClickListener {
            finish()
        }

        // 앱 시작하기
        binding.txtClauseStart.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            ActivityCompat.finishAffinity(this)
        }
    }
}