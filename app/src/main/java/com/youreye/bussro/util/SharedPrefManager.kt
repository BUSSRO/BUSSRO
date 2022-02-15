package com.youreye.bussro.util

import android.content.Context
import com.google.gson.Gson

object SharedPrefManager {
    private const val FIRST = "first"
    private const val FIRST_KEY = "first_key"

    private const val TTS = "tts"
    private const val TTS_KEY = "tts_key"

    private const val SEARCH_HISTORY = "search_history"
    private const val SEARCH_HISTORY_KEY = "search_history_key"

    /* 앱 접속이력 저장 */
    fun setFirst(context: Context, flag: Boolean) {
        val sharedPreferences = context.getSharedPreferences(FIRST, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(FIRST_KEY, flag).apply()
    }

    /* 앱 접속이력 가져오기 */
    fun isFirst(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(FIRST, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(FIRST_KEY, true)
    }

    /* TTS 설정 저장 */
    fun setTTS(context: Context, flag: Boolean) {
        val sharedPreferences = context.getSharedPreferences(TTS, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(TTS_KEY, flag).apply()
    }

    /* TTS 설정 가져오기 */
    fun getTTS(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(TTS, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(TTS_KEY, true)
    }

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