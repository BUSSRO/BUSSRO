package com.youreye.bussro.feature.search

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.youreye.bussro.R
import com.youreye.bussro.databinding.ActivitySearchBinding
import com.youreye.bussro.util.SharedPrefManager
import com.youreye.bussro.util.logd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity(), ISearchRecyclerView {
    private lateinit var binding: ActivitySearchBinding
    private var searchHistory = ArrayList<String>()
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        binding.activity = this
        binding.lifecycleOwner = this

        initVar()
        handleUI()
    }

    private fun initVar() {
        /* 검색 기록 불러오기 */
        searchHistory = SharedPrefManager.getSearchHistory(this) as ArrayList<String>

        /* 뒤로가기 */
        binding.ibSearchBack.setOnClickListener {
            finish()
        }

        /* 음성인식 */
        binding.ivSearchVoice.setOnClickListener {
            // TODO: 구현
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

                if (term.isNotEmpty()) {
                    insertSearchHistory(term)

                    // 검색어 초기화
                    v.text = ""

                    // 키보드 동작 제어
                    val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                } else {
                    // 검색어를 입력하지 않은 경우
                    Toast.makeText(this, "검색어를 입력하세요.", Toast.LENGTH_SHORT).show()
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
        logd("$searchHistory")

        // 데이터 저장
        SharedPrefManager.setSearchHistory(this, searchHistory)
        searchAdapter.updateData(searchHistory)

        handleUI()
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

        // TODO: 해당 position 의 검색어로 API 호출

        insertSearchHistory(queryString)
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
}