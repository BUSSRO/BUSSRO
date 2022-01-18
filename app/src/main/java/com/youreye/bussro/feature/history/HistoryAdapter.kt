package com.youreye.bussro.feature.history

import android.app.Application
import android.content.Context
import android.content.Intent
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import android.widget.ToggleButton
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvHistoryItemBinding
import com.youreye.bussro.feature.buslist.BusListActivity
import com.youreye.bussro.feature.buslist.CustomDialog
import com.youreye.bussro.model.db.entity.History
import com.youreye.bussro.model.repository.HistoryRepository
import java.text.SimpleDateFormat
import java.util.*

/**
 * [HistoryAdapter]
 * HistoryActivity 의 RecyclerView 의 Adapter
 *
 * @param supportFragmentManager supportFragmentManager from HistoryActivity
 * @param application application from HistoryActivity
 */

class HistoryAdapter(
    private val supportFragmentManager: FragmentManager,
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
        /* ViewBinding 에 데이터 insert */
        holder.binding.history = data[position]

        /* txt_history_rtNm_and_date 데이터 초기화 */
        // Date 형식 지정
        val dateFormat = SimpleDateFormat("yy.MM.dd hh:mm", Locale.getDefault())
        val date = dateFormat.format(data[position].date)
        val rtNmAndDate = "${data[position].rtNm} • $date"

        // Date 색상 변경
        val builder = SpannableStringBuilder(rtNmAndDate)
        val colorSpan = ForegroundColorSpan(application.resources.getColor(R.color.light_gray))
//        builder.setSpan(colorSpan, rtNmAndDate.indexOf("•" + 1), rtNmAndDate.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(colorSpan, data[position].rtNm!!.toString().length + 1, rtNmAndDate.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        holder.binding.txtHistoryRtNmAndDate.text = builder

        /* 항목 click listener */
        holder.binding.root.setOnClickListener {
            // 버스 탑승 dialog 띄우기
            val dialog = CustomDialog(
                data[position].rtNm,
                data[position].stationNm,
                data[position].arsId
            )
            dialog.show(supportFragmentManager, "FromHistoryActivity")
        }

        /* ToggleButton checked change listener */
        holder.binding.tbHistory.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                Toast.makeText(application, "${data[position].rtNm} 즐겨찾기 등록", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(application, "${data[position].rtNm} 즐겨찾기 해제", Toast.LENGTH_SHORT).show()
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