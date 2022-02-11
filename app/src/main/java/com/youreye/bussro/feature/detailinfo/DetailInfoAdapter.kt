package com.youreye.bussro.feature.detailinfo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvDetailInfoItemBinding
import com.youreye.bussro.model.DetailInfoData

class DetailInfoAdapter(
) : RecyclerView.Adapter<DetailInfoAdapter.DetailInfoViewHolder>() {
    private var manual = mutableListOf<DetailInfoData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailInfoViewHolder {
        val binding: RvDetailInfoItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_detail_info_item,
            parent,
            false
        )

        return DetailInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailInfoViewHolder, position: Int) {
        holder.bind(manual[position])
    }

    override fun getItemCount(): Int = manual.size

    fun updateData(data: MutableList<DetailInfoData>) {
        manual = data
        notifyDataSetChanged()
    }

    inner class DetailInfoViewHolder(@NonNull val binding: RvDetailInfoItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DetailInfoData) {
            // 타이틀
            binding.txtDetailInfoTitle.text = data.title

            // 설명
            binding.txtDetailInfoTitleDesc.text = data.desc
        }
    }
}