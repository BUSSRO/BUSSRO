package com.youreye.bussro.feature.search

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvListItemBinding

class SearchAdapter(
    private val iSearchRecyclerView: ISearchRecyclerView
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {
    private var data = listOf<String>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchAdapter.SearchViewHolder {
        val binding: RvListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_list_item,
            parent,
            false
        )

        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchAdapter.SearchViewHolder, position: Int) {
        /* 검색어 data input */
        holder.binding.txtListItemStSrch.text = data[position]

        /* 항목 click listener */
        holder.binding.root.setOnClickListener {
            iSearchRecyclerView.onSearchItemClicked(position)
        }

        /* 지우기 click listener */
        holder.binding.ivListItemDelete.setOnClickListener {
            iSearchRecyclerView.onSearchItemDeleteClicked(position)
        }
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(newData: List<String>) {
        this.data = newData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class SearchViewHolder(@NonNull val binding: RvListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }
}