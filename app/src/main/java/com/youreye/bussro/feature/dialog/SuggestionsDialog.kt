package com.youreye.bussro.feature.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentSuggestionsDialogBinding
import com.youreye.bussro.util.logd

class SuggestionsDialog : DialogFragment() {
    private lateinit var binding: FragmentSuggestionsDialogBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_suggestions_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 배경색(투명)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        /* 취소 */
        binding.ivSuggestionsClose.setOnClickListener {
            dismiss()
        }

        /* 전송 */
        binding.txtSuggestionsSend.setOnClickListener {
            // 문자열 hashMap 으로 변환
            val content = hashMapOf("content" to binding.edtSuggestionsContent.text.toString())

            // 서버로 전송
            db.collection("suggestions")
                .add(content)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "소중한 의견 감사합니다!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "전송 실패했어요", Toast.LENGTH_SHORT).show()
                    logd("addOnFailureListener, $it")
                }
        }

        /* EditText TextChanged Listener */
        binding.edtSuggestionsContent.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.length < 20) {
                        binding.txtSuggestionsSend.setTextColor(resources.getColor(R.color.black))
                        binding.txtSuggestionsSend.isClickable = false
                    } else {
                        binding.txtSuggestionsSend.setTextColor(resources.getColor(R.color.yellow))
                        binding.txtSuggestionsSend.isClickable = true
                    }
                } else {
                    binding.txtSuggestionsSend.setTextColor(resources.getColor(R.color.black))
                    binding.txtSuggestionsSend.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }
}