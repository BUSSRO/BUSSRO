package com.youreye.bussro.feature.buslist

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentBoardingDialogBinding
import com.youreye.bussro.feature.sign.SignActivity
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.examples.detection.DetectorActivity
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class BoardingDialog(
    private val rtNm: String,
    private val stationNm: String,
    private val arsId: String
) : DialogFragment() {
    private lateinit var binding: FragmentBoardingDialogBinding
    @Inject lateinit var historyRepository: HistoryRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_boarding_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDialog()
        initVar()
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
            dismiss()
        }

        /* 카메라 기능 */
        binding.txtDialogUseCamera.setOnClickListener {
            val intent = Intent(view?.context, DetectorActivity::class.java)
                .putExtra("rtNm", rtNm)
            startActivity(intent)
            insertHistory()
            dismiss()
        }
    }

    /* DB 에 히스토리 저장 */
    private fun insertHistory() {
        // date 형식 지정
        val dateFormat = SimpleDateFormat("yy.MM.dd hh:mm:ss", Locale.getDefault())
        val date = dateFormat.format(System.currentTimeMillis())


        historyRepository.insert(
            History(
                date,
                rtNm,
                arsId,
                stationNm,
                false
            )
        )
    }
}