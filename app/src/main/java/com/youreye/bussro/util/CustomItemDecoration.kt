package com.youreye.bussro.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * [CustomItemDecoration]
 * RecyclerView 아이템 간 Custom 설정을 주기 위함
 *
 * @param padding 띄우고 싶은 값
 * @param height divider 높이
 * @param color divider 색상
 */

class CustomItemDecoration(
    private val padding: Int,
    private val divPadding: Float,
    private val height: Float,
    private val color: Int
) : RecyclerView.ItemDecoration() {
    private val paint = Paint()

    init {
        paint.color = color
    }

    /* Item 간의 padding 설정 */
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = padding
    }

    /* Item 간의 divider 설정 */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingStart + divPadding + 30
        val right = parent.width - parent.paddingEnd - divPadding - 30

        for (i in 0 until parent.childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            val top = (child.bottom + params.bottomMargin).toFloat()
            val bottom = top + height

            c.drawRect(left, top, right, bottom, paint)
        }
    }
}

@BindingAdapter(
    value = ["itemPadding", "dividerHeight", "dividerPadding", "dividerColor"],
    requireAll = false
)
fun RecyclerView.setDivider(
    itemPadding: Int?,
    dividerPadding: Float?,
    dividerHeight: Float?,
    @ColorInt dividerColor: Int?
) {
    val decoration = CustomItemDecoration(
        padding = itemPadding ?: 0,
        divPadding = dividerPadding ?: 0f,
        height = dividerHeight ?: 0f,
        color = dividerColor ?: Color.TRANSPARENT
    )

    addItemDecoration (decoration)
}
