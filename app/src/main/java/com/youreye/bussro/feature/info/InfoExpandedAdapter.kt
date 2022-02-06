package com.youreye.bussro.feature.info

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvSimpleItemBinding

class InfoExpandedAdapter(
    private val category: Array<String>
) : RecyclerView.Adapter<InfoExpandedAdapter.InfoExpandedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoExpandedViewHolder {
        val binding: RvSimpleItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_simple_item,
            parent,
            false
        )

        return InfoExpandedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InfoExpandedViewHolder, position: Int) {
        holder.bind(category[position], position)
    }

    override fun getItemCount(): Int = category.size

    inner class InfoExpandedViewHolder(@NonNull val binding: RvSimpleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: String, position: Int) {
            /* 텍스트 초기화 */
            binding.txtSimpleItem.text = data

            /* background 초기화 */
//            val layout = if (position % 2 == 0) R.drawable.rv_left_item_border else R.drawable.rv_right_item_border
//            binding.llSimple.setBackgroundResource(layout)

            /* click event */
            binding.root.setOnClickListener {
                val intent = Intent(it.context, DetailInfoActivity::class.java)
                    .putExtra("category", data)
                it.context.startActivity(intent)
            }
        }
    }
}