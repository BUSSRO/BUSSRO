package com.youreye.bussro.util

import android.content.Context
import com.google.gson.Gson

object SharedPrefManager {
    private const val SEARCH_HISTORY = "search_history"
    private const val SEARCH_HISTORY_KEY = "search_history_key"

    /* 검색어 저장 */
    fun setSearchHistory(context: Context, searchHistory: MutableList<String>) {
        val sharedPreferences = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)

        // 배열(매개변수) -> 문자열 변환
        val data = Gson().toJson(searchHistory)
        sharedPreferences.edit().putString(SEARCH_HISTORY_KEY, data).apply()
    }

    /* 검색 목록 가져오기 */
    fun getSearchHistory(context: Context) : MutableList<String> {
        val sharedPreferences = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        val searchHistory = sharedPreferences.getString(SEARCH_HISTORY_KEY, "")!!

        var data = ArrayList<String>()
        if (searchHistory.isNotEmpty()) {
            // 문자열 -> 배열로 변환
            data = Gson().fromJson(searchHistory, Array<String>::class.java).toMutableList() as ArrayList<String>
        }

        return data
    }

    /* 검색 목록 전체 삭제 */
    fun deleteSearchHistory(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().apply()
    }
}