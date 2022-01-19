package com.youreye.bussro.feature.search

interface ISearchRecyclerView {
    // 히스토리 삭제 버튼 클릭
    fun onSearchItemDeleteClicked(position: Int)

    // 히스토리 항목 선택
    fun onSearchItemClicked(position: Int)
}