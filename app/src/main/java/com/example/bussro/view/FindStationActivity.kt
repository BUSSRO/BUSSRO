package com.example.bussro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bussro.R
import com.example.bussro.databinding.ActivityFindStationBinding

/**
 * [FindStationActivity]
 * MainActivity 의 "내 주변 정류장" 버튼을 클릭했을시 보여짐
 * TTS & STT 를 이용해서 사용자가 찾는 정류장을 입력받는다.
 *
 * @author 윤주연(otu165)
 */

class FindStationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindStationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_station)
    }
}