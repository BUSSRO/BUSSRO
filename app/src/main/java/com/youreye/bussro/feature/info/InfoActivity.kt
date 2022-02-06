package com.youreye.bussro.feature.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityInfoBinding
import com.youreye.bussro.model.InfoData

class InfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoBinding
    private lateinit var infoRvAdapter: InfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_info)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initClickListener()
        initVar()
    }

    private fun initVar() {
        val data = getInfoData()
        infoRvAdapter = InfoAdapter(data)

        binding.rvInfo.apply {
            adapter = infoRvAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    /* 앱 사용설명서 목록 데이터 불러오기 */
    private fun getInfoData(): List<InfoData> {
        val category = resources.getStringArray(R.array.info_list)
        val data = mutableListOf<InfoData>()

        for (c in category) {
            data.add(InfoData(c))
        }

        return data
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.ivSettingInfoBack.setOnClickListener {
            this.finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}