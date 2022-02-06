package com.youreye.bussro.feature.info

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvInfoItemBinding
import com.youreye.bussro.model.InfoData
import com.youreye.bussro.util.ToggleAnimation

class InfoAdapter(
    private val category: List<InfoData>
) : RecyclerView.Adapter<InfoAdapter.InfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val binding: RvInfoItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_info_item,
            parent,
            false
        )

        return InfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.bind(category[position], position)
    }

    override fun getItemCount(): Int = category.size

    inner class InfoViewHolder(@NonNull val binding: RvInfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: InfoData, position: Int) {
            /* 텍스트 속성 변경 */
            binding.txtInfoItemTitle.text = data.category

            /* 리스트 클릭 리스너 */
            binding.root.setOnClickListener {
                binding.ivInfoItemExpand.callOnClick()
            }

            /* 더보기 리스너 */
            binding.ivInfoItemExpand.setOnClickListener {
                val show = toggleLayout(!data.isExpanded, it, binding.clInfoBottom)
                data.isExpanded = show
            }

            /* RecyclerView 초기화 */
            val expandedCategory = when (position) {
                0 -> {
                    binding.root.context.resources.getStringArray(R.array.info_first_list)
                }
                1 -> {
                    binding.root.context.resources.getStringArray(R.array.info_second_list)
                }
                else -> {
                    binding.root.context.resources.getStringArray(R.array.info_third_list)
                }
            }

            if (expandedCategory.isEmpty()) {
                binding.ivInfoItemExpand.visibility = View.GONE
            }

            val rvAdapter = InfoExpandedAdapter(expandedCategory)
            binding.rvInfoBottom.apply {
                adapter = rvAdapter
                layoutManager = GridLayoutManager(this.context, 2)
            }
        }

        /* 레이아웃 확장 or 숨기기 */
        private fun toggleLayout(
            isExpanded: Boolean,
            view: View,
            layoutExpand: ConstraintLayout
        ): Boolean {
            // 이미지 제어
            ToggleAnimation.toggleArrow(view, isExpanded)

            // 레이아웃 제어
            if (isExpanded) {
                ToggleAnimation.expand(layoutExpand)
            } else {
                ToggleAnimation.collapse(layoutExpand)
            }

            return isExpanded
        }
    }
}