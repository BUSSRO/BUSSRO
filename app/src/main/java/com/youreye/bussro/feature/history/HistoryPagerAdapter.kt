package com.youreye.bussro.feature.history

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository

class HistoryPagerAdapter(
    private val supportFragmentManager: FragmentManager,
    private val application: Application,
    private val historyRepository: HistoryRepository
) : RecyclerView.Adapter<HistoryPagerAdapter.HistoryPagerViewHolder>() {
    var rvAdapter = HistoryAdapter(supportFragmentManager, application, historyRepository)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryPagerAdapter.HistoryPagerViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.vp_history_item, parent, false)
        return HistoryPagerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: HistoryPagerAdapter.HistoryPagerViewHolder,
        position: Int
    ) {
        holder.bind()
    }

    override fun getItemCount(): Int = 2

    /* 데이터 갱신 */
    fun updateData(data: List<History>) {
        rvAdapter.updateData(data)
    }

    inner class HistoryPagerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val rv: RecyclerView = view.findViewById(R.id.rv_history_vp_item)

        fun bind() {
            rv.apply {
                adapter = rvAdapter
                layoutManager = LinearLayoutManager(view.context)
            }
        }
    }
}