package com.youreye.bussro.feature.search

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivitySearchBinding
import com.youreye.bussro.util.ErrorHandlerManager
import com.youreye.bussro.util.SharedPrefManager
import com.youreye.bussro.util.logd
import java.util.*
import kotlin.collections.ArrayList


class SearchActivity : AppCompatActivity(), ISearchRecyclerView, TextToSpeech.OnInitListener  {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var binding: ActivitySearchBinding
    private var searchHistory = ArrayList<String>()
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var tts: TextToSpeech
    private var isTtsSettingDone = false
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ErrorHandlerManager.setCrashHandler(application)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.activity = this
        binding.lifecycleOwner = this
        overridePendingTransition(R.anim.enter_from_right, R.anim.fade_out)

        // 음성인식 옵저빙
        viewModel.searchedStationByVoice.observe(this, {
            insertSearchHistory(it)
        })


        initVar()
        handleUI()
    }

    private fun initVar() {
        /* 검색 기록 불러오기 */
        searchHistory = SharedPrefManager.getSearchHistory(this) as ArrayList<String>

        /* 뒤로가기 */
        binding.ibSearchBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }

        /* TextToSpeech 객체 */
        tts = TextToSpeech(this, this)

        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                    if (!data.isNullOrEmpty() && data[0].toString().length > 1) {
                        // 음성인식 결과 확인
                        val searchTerm = data[0].toString()

                        insertSearchHistory(searchTerm)

                        // result 설정
                        val intent = Intent().putExtra("station", searchTerm)
                        setResult(Activity.RESULT_OK, intent)

                        finish()

                    } else {
                        Toast.makeText(this, "검색어는 두 글자 이상 말해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        /* SpeechToText 객체 */
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            .putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")

        /* 음성인식 */
        binding.ivSearchVoice.setOnClickListener {
            VoiceDialog().show(supportFragmentManager, "VoiceDialog")
        }

        /* RecyclerView 초기화 */
        searchAdapter = SearchAdapter(this)
        searchAdapter.updateData(searchHistory)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        linearLayoutManager.stackFromEnd = true

        binding.rvSearch.apply {
            this.layoutManager = linearLayoutManager
            this.scrollToPosition(searchAdapter.itemCount - 1)
            this.adapter = searchAdapter
        }

        /* EditText Search 감지 */
        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 검색
                val term = v.text.toString()

                if (term.length > 1) {
                    insertSearchHistory(term)

                    // 검색어 초기화
                    v.text = ""

                    // 키보드 동작 제어
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                } else {
                    // 검색어를 입력하지 않은 경우
                    Toast.makeText(this, "검색어는 두 글자 이상 입력하세요.", Toast.LENGTH_SHORT).show()
                    v.requestFocus()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        /* 전체 삭제 */
        binding.txtSearchDeleteAll.setOnClickListener {
            SharedPrefManager.deleteSearchHistory(this@SearchActivity)
            searchHistory.clear()
            searchAdapter.updateData(listOf())

            handleUI()
        }
    }

    /* 검색어 저장 */
    private fun insertSearchHistory(term: String) {
        // 중복 검색어 삭제
        val indexListToRemove = ArrayList<Int>()

        searchHistory.forEachIndexed { index, data ->
            if (data == term) {
                indexListToRemove.add(index)
            }
        }

        indexListToRemove.forEach {
            searchHistory.removeAt(it)
        }

        // 새 검색어 저장
        searchHistory.add(term)

        // 7개 초과시 가장 오래된 검색기록 삭제
        if (searchHistory.size > 7) {
            searchHistory.removeAt(0)
        }

        // 데이터 저장
        SharedPrefManager.setSearchHistory(this, searchHistory)
        searchAdapter.updateData(searchHistory)

        // CHECK: handleUI() 필요한가?
        handleUI()

        // result 설정
        val intent = Intent().putExtra("station", term)
        setResult(Activity.RESULT_OK, intent)

        logd("setResult 설정, station : $term")

        finish()
    }

    /* 특정 검색어 삭제 */
    override fun onSearchItemDeleteClicked(position: Int) {
        // 검색어 삭제
        searchHistory.removeAt(position)

        SharedPrefManager.setSearchHistory(this, searchHistory)
        searchAdapter.notifyDataSetChanged()

        handleUI()
    }

    /* 검색어 항목 선택 */
    override fun onSearchItemClicked(position: Int) {
        val queryString = searchHistory[position]
        insertSearchHistory(queryString)

        // result 설정
        val intent = Intent().putExtra("station", queryString)
        setResult(Activity.RESULT_OK, intent)

        logd("setResult 설정, station : $queryString")

//        finish()
    }

    /* 최근검색어 유무에 따라 UI 바꿔서 보여주기 */
    private fun handleUI() {
        if (searchHistory.size > 0) {
            // 있는 경우 : 최근검색어 리스트
            binding.ivSearchPlaceholder.visibility = View.GONE
            binding.txtSearchPlaceholder.visibility = View.GONE

            binding.rvSearch.visibility = View.VISIBLE
        } else {
            // 없는 경우 : placeholder
            binding.ivSearchPlaceholder.visibility = View.VISIBLE
            binding.txtSearchPlaceholder.visibility = View.VISIBLE
        }
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
                isTtsSettingDone = true
            }
        } else {
            Toast.makeText(this, "TTS를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestSTT() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "찾는 정류장을 말해주세요.")  // 예시로 보여지는 텍스트
            }

            startActivityForResult.launch(intent)

        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "STT를 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
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
        if (tts.isSpeaking) {
            tts.stop()
        }

        super.onBackPressed()
        if (isFinishing) {
            overridePendingTransition(R.anim.fade_in, R.anim.exit_to_right)
        }
    }
}