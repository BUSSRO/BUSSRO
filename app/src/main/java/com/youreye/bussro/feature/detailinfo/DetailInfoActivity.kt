package com.youreye.bussro.feature.detailinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivityDetailInfoBinding
import com.youreye.bussro.feature.dialog.SuggestionsDialog
import com.youreye.bussro.model.DetailInfoData
import com.youreye.bussro.util.SharedPrefManager
import java.util.*
import kotlin.collections.HashMap

class DetailInfoActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var binding: ActivityDetailInfoBinding
    private lateinit var rvAdapter: DetailInfoAdapter
    private lateinit var id: String
    private lateinit var tts: TextToSpeech
    private val admitTTS by lazy {
        SharedPrefManager.getTTS(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_info)
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        initClickListener()
        initVar()
    }

    private fun initClickListener() {
        /* 뒤로가기 */
        binding.ivDetailInfoBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* 문의하러가기 */
        binding.txtDetailInfo.setOnClickListener {
            SuggestionsDialog().show(supportFragmentManager, "SuggestionsDialog")
        }
    }

    private fun initVar() {
        /* document id 초기화 */
        id = intent.getStringExtra("categoryId")!!
        Log.d("TEST", "categoryId: $id")

        /* 선택한 항목 안내 초기화 */
        val category = intent.getStringExtra("category") ?: ""
        binding.txtDetailInfoTitle.text = category

        /* RecyclerView 초기화 */
        rvAdapter = DetailInfoAdapter()
        binding.rvDetailInfo.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@DetailInfoActivity)
        }

        /* TTS 객체 초기화 */
        tts = TextToSpeech(this, this)

        getManualFromFireStore()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 언어 설정
            val locale = Locale("ko", "KR")
            val result = tts.setLanguage(locale)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // TTS 사용 가능
            }
        } else {
            Toast.makeText(this, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    /* 매뉴얼 가져오기 */
    private fun getManualFromFireStore() {
        // 로딩 시작
        binding.progressDetailInfo.visibility = View.VISIBLE

        val db = FirebaseFirestore.getInstance()

        db.document("manuals/manual").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 로딩 끝
                    binding.progressDetailInfo.visibility = View.GONE

                    val document: DocumentSnapshot = task.result

                    // null 처리
                    if (document.data == null) {
                        // 음성안내
                        if (admitTTS) {
                            tts.speak(
                                "불러오기 실패",
                                TextToSpeech.QUEUE_FLUSH,
                                null,
                                TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                            )
                        }
                        return@addOnCompleteListener
                    }

                    val data = mutableListOf<DetailInfoData>()
                    val list = document.data!!.get(id) as List<Any>

                    for (element in list) {
                        val hashMap = element as HashMap<String, String>
                        val title = hashMap.get("title")!!
                        val desc = hashMap.get("desc")!!

                        data.add(DetailInfoData(title, desc))
                    }

                    rvAdapter.updateData(data)

                    // 음성안내
                    if (admitTTS) {
                        tts.speak(
                            "불러오기 완료",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                        )
                    }
                }
            }
            .addOnFailureListener {
                // 앱 사용설명서 불러오기 실패
                Log.d("TEST", "getManualFromFireStore_error: $it")

                // 로딩 끝
                binding.progressDetailInfo.visibility = View.GONE
                // 음성안내
                if (admitTTS) {
                    tts.speak(
                        "불러오기 실패",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED
                    )
                }
            }
    }

    override fun onStop() {
        tts.stop()
        super.onStop()
    }

    override fun onDestroy() {
        tts.shutdown()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}