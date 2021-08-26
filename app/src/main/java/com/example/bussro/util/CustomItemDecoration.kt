package com.example.bussro.util

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * [CustomItemDecoration]
 * RecyclerView 아이템 간 Custom 설정을 주기 위함
 * @param padding 띄우고 싶은 값
 *
 * @author 윤주연(otu165)
 */

class CustomItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    // Item 간의 padding 설정
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = padding
    }
}