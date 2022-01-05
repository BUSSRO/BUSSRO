package com.youreye.bussro.feature.buslist

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentCustomDialogBinding
import com.youreye.bussro.feature.sign.SignActivity
import org.tensorflow.lite.examples.detection.DetectorActivity

class CustomDialog : DialogFragment() {
    private lateinit var binding: FragmentCustomDialogBinding

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
                .putExtras(bundleOf("busList" to (activity as BusListActivity).busList))
                .putExtra("rtNm", (activity as BusListActivity).busList[0])
            startActivity(intent)
        }

        /* 카메라 기능 */
        binding.txtDialogUseCamera.setOnClickListener {
            val intent = Intent(view?.context, DetectorActivity::class.java)
                .putExtra("rtNm", (activity as BusListActivity).busList[0])
            startActivity(intent)
        }
    }
}