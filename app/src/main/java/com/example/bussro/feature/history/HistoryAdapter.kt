package com.example.bussro.feature.history

import android.app.Application
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.bussro.R
import com.example.bussro.databinding.RvHistoryItemBinding
import com.example.bussro.feature.buslist.BusListActivity
import com.example.bussro.model.db.entity.History
import com.example.bussro.model.repository.HistoryRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * [HistoryAdapter]
 * HistoryActivity 의 RecyclerView 의 Adapter
 *
 * @param application application from HistoryActivity
 */

class HistoryAdapter(
    private val historyRepository: HistoryRepository,
    private val application: Application
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
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
            root.setOnClickListener { view ->
                val intent = Intent(view.context, BusListActivity::class.java)
                    .putExtra("stationNm", history?.stationNm)
                    .putExtra("arsId", history?.arsId)
                view.context.startActivity(intent)

                // 현재 일자 및 시각
                val date = Date(System.currentTimeMillis())
                val dateFormat = SimpleDateFormat("yy-MM-dd\nhh:mm:ss", Locale.getDefault())

                // History 입력
//                val repository = HistoryRepository(application)
                historyRepository.insert(
                    History(
                        history?.arsId!!,
                        history?.stationNm!!,
                        dateFormat.format(date)
                    )
                )
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