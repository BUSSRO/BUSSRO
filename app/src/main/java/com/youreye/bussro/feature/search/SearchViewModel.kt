package com.youreye.bussro.feature.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SearchViewModel : ViewModel() {
    val searchedStationByVoice = MutableLiveData<String>()
}