package com.youreye.bussro.feature.buslist

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentCustomDialogBinding
import com.youreye.bussro.feature.sign.SignActivity
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.detection.DetectorActivity
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CustomDialog : DialogFragment() {
    private lateinit var binding: FragmentCustomDialogBinding
    @Inject lateinit var historyRepository: HistoryRepository
    private var rtNm = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_custom_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()
        initVar()
    }

    /* 버스번호 지정 */
    fun setRtNm(rtNm: String) {
        this.rtNm = rtNm
    }

    private fun initDialog() {
        // 배경색(투명)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initVar() {
        /* 뒤로가기 */
        binding.ibDialogBack.setOnClickListener {
            dismiss()
        }

        /* 전광판 기능 */
        binding.txtDialogUseSign.setOnClickListener {
            val intent = Intent(view?.context, SignActivity::class.java)
                .putExtra("rtNm", rtNm)
            startActivity(intent)
            insertHistory()
        }

        /* 카메라 기능 */
        binding.txtDialogUseCamera.setOnClickListener {
            val intent = Intent(view?.context, DetectorActivity::class.java)
                .putExtra("rtNm", rtNm)
            startActivity(intent)
            insertHistory()
        }
    }

    /* DB 에 히스토리 저장 */
    private fun insertHistory() {
        historyRepository.insert(
            History(
                Date(System.currentTimeMillis()),
                rtNm
            )
        )
    }
}