package com.example.bussro.feature.history

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.R
import com.example.bussro.databinding.RvHistoryItemBinding
import com.example.bussro.model.db.entity.History

/**
 * [HistoryAdapter]
 * HistoryActivity 의 RecyclerView 의 Adapter
 */

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    private var data = listOf<History>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding: RvHistoryItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_history_item,
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.binding.apply {
            // 데이터 input
            history = data[position]
            // click event
            root.setOnClickListener {
                // TODO: BusListActivity 로 넘어가기
                Log.d("test", "onBindViewHolder: clicked")
            }
        }
    }

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(data: List<History>) {
        this.data = data
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class HistoryViewHolder(@NonNull val binding: RvHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}