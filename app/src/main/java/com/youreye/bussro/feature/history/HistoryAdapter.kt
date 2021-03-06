package com.youreye.bussro.feature.history

import android.app.Application
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvHistoryItemBinding
import com.youreye.bussro.di.App
import com.youreye.bussro.feature.dialog.BoardingDialog
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository

/**
 * [HistoryAdapter]
 * HistoryActivity 의 ViewPager2 의 RecyclerView 의 Adapter
 *
 * @param supportFragmentManager supportFragmentManager from HistoryActivity
 * @param application application from HistoryActivity
 * @param historyRepository History DB 조작을 위한 repository
 */

class HistoryAdapter(
    private val supportFragmentManager: FragmentManager,
    private val application: Application,
    private val historyRepository: HistoryRepository
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
        /* ViewBinding 에 데이터 insert */
        holder.binding.history = data[position]

        /* txt_history_rtNm_and_date 데이터 초기화 */
        // text 완성
        val date = data[position].date
        val rtNmAndDate = "${data[position].rtNm} • ${date.substring(0, date.length - 3)}"

        // Date 색상 변경
        val builder = SpannableStringBuilder(rtNmAndDate)
        val colorSpan = ForegroundColorSpan(application.resources.getColor(R.color.light_gray))
        builder.setSpan(colorSpan, data[position].rtNm.length + 1, rtNmAndDate.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.binding.txtHistoryRtNmAndDate.text = builder

        /* 항목 content description */
        val contentDescription = "${data[position].stationNm} 정류장의 ${data[position].rtNm}번 버스"
        holder.binding.root.contentDescription = contentDescription

        /* 항목 click listener */
        holder.binding.root.setOnClickListener {
            // 버스 탑승 dialog 띄우기
            val dialog = BoardingDialog(
                data[position].rtNm,
                data[position].stationNm,
                data[position].arsId
            )

            dialog.show(supportFragmentManager, "FromHistoryActivity")
        }

        /* 히스토리 스크랩 여부 갱신 */
        holder.binding.tbHistory.setOnClickListener {
            historyRepository.update(data[position])
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