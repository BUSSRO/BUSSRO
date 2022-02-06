package com.youreye.bussro.feature.search

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.youreye.bussro.R
import com.youreye.bussro.databinding.FragmentVoiceDialogBinding
import com.youreye.bussro.util.logd

class VoiceDialog : DialogFragment() {
    private val viewModel: SearchViewModel by activityViewModels()
    private lateinit var binding: FragmentVoiceDialogBinding
    private lateinit var listener: RecognitionListener
    private lateinit var recognizer: SpeechRecognizer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_voice_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // full screen
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        // 배경색(투명)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 권한 체크
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO),
                1
            )
        }

        // gif 넣기
        Glide.with(view).load(R.raw.voice2).into(binding.ivVoice)

        // SpeechToText 객체
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, requireActivity().packageName)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        initRecognitionListener()

        recognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        recognizer.setRecognitionListener(listener)
        recognizer.startListening(sttIntent)

//        Glide.with(view).load(R.raw.g1).into(binding.ivVoice)

        /* 이미지 클릭 리스너 */
        binding.ivVoice.setOnClickListener {
            recognizer.startListening(sttIntent)
        }

//        binding.lottieVoice.setOnClickListener {
//            recognizer.startListening(sttIntent)
//        }
    }

    private fun initRecognitionListener() {
        listener = object: RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                logd("음성 인식 시작")

                playStt(true, "찾는 정류장을 말해주세요.")
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            @SuppressLint("SetTextI18n")
            override fun onError(error: Int) {
                val msg = when(error) {
                    SpeechRecognizer.ERROR_AUDIO -> { "오디오 에러" }
                    SpeechRecognizer.ERROR_CLIENT -> { "클라이언트 에러" }
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS-> { "퍼미션 없음" }
                    SpeechRecognizer.ERROR_NETWORK -> { "네트워크 에러" }
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> { "네트워크 타임아웃" }
                    SpeechRecognizer.ERROR_NO_MATCH -> { "인식할 수 없음" }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> { "recognizer 바쁨" }
                    SpeechRecognizer.ERROR_SERVER -> { "서버 에러" }
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> { "시간초과" }
                    else -> { "알 수 없는 에러" }
                }

                logd("음성인식 문제 발생, 사유 : $msg")

                playStt(false, "음성을 인식할 수 없어요.\n이미지를 클릭한 후 다시 말해주세요.")
            }

            @SuppressLint("SetTextI18n")
            override fun onResults(results: Bundle?) {
                // 음성인식 결과
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                var text = ""
                if (matches != null && matches.isNotEmpty()) {
                    for (word in matches.withIndex()) {
                        text += matches[word.index]
                    }
                }

                // 텍스트 공백제거
                text = text.replace(" ","")
                logd("STT_final_result : $text")

                // 검색어 확인
                if (text.length > 1) {
                    playStt(true, "$text 정류장을 검색할게요.")

                    Handler().postDelayed({
                        viewModel.searchedStationByVoice.postValue(text)
                    }, 1500)
                } else {
                    playStt(false, "두 글자 이상 말해주세요.\n이미지를 클릭한 후 다시 말해주세요.")
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }
        }
    }

    private fun playStt(play: Boolean, text: String) {
        // lottie 애니메이션 제어
        if (play) {
            binding.lottieVoice.playAnimation()
        } else {
            binding.lottieVoice.pauseAnimation()
            binding.lottieVoice.progress = 0F
        }

        // 텍스트 수정
        binding.txtVoice.text = text
    }

    override fun onDetach() {
        recognizer.cancel()

        super.onDetach()
    }

    override fun onDestroy() {
        recognizer.destroy()
        logd("VoiceDialog_onDestroy")

        super.onDestroy()
    }
}