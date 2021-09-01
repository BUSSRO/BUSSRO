package com.example.bussro.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityMainBinding
import com.example.bussro.view.BusListActivity
import com.example.bussro.view.FindStationActivity

/**
 * [MainActivity]
 * SplashActivity 후 보여짐
 * 사용자는 원하는 버튼을 선택해서 원하는 기능으로 이동할 수 있다.
 *
 * @author 윤주연(otu165)
 */

class MainActivity : AppCompatActivity() {
    private val model by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
        binding.model = model
    }
}