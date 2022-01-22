package com.youreye.bussro.feature.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentBackPressDialogBinding

/**
 * [BackPressDialog]
 * 뒤로가기 선택시 앱을 종료할건지 물어볼 Custom Dialog
 */

class BackPressDialog : DialogFragment() {
    private lateinit var binding: FragmentBackPressDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_back_press_dialog, container, false)
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
        binding.btnBpdNo.setOnClickListener {
            dismiss()
        }

        binding.btnBpdYes.setOnClickListener {
            dismiss()
            requireActivity().finish()
        }
    }
}