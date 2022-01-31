package com.youreye.bussro.feature.buslist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.youreye.bussro.model.network.response.BusListData
import com.youreye.bussro.R
import com.youreye.bussro.databinding.RvBusListItemBinding
import com.youreye.bussro.di.App
import com.youreye.bussro.feature.dialog.BoardingDialog
import com.youreye.bussro.util.ToggleAnimation
import de.hdodenhof.circleimageview.CircleImageView

/**
 * [BusListAdapter]
 * BusListActivity 의 RecyclerView 에 연결될 Adapter
 *
 * @param activity BusListActivity
 */

class BusListAdapter(
    private val activity: BusListActivity,
    private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<BusListAdapter.BusListViewHolder>() {
    private var data = listOf<BusListData.MsgBody.BusList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusListViewHolder {
        val binding: RvBusListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_bus_list_item,
            parent,
            false
        )
        return BusListViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BusListViewHolder, position: Int) {
        /* ViewBinding 에 data insert */
        holder.binding.busList = data[position]

        /* expand & collapse click listener */
        holder.binding.ivBusListItemExpand.setOnClickListener {
            val show = toggleLayout(!data[position].isExpanded, it, holder.binding.clBottom)
            data[position].isExpanded = show

            setContentDescription(holder, position)
        }

        /* expand & collapse content description */
        setContentDescription(holder, position)

        /* 항목 click listener */
        holder.binding.root.setOnClickListener {
            val dialog = BoardingDialog(
                data[position].rtNm,
                data[position].stNm,
                data[position].arsId
            )

            dialog.show(supportFragmentManager, "FromBusListActivity")
        }

        /* 운행시간 */
        val firstTm =
            data[position].firstTm.substring(0, 2) + ":" + data[position].firstTm.subSequence(2, 4)
        val lastTm =
            data[position].lastTm.substring(0, 2) + ":" + data[position].lastTm.subSequence(2, 4)
        holder.binding.txtBusListTime.text = "첫차 $firstTm, 막차 $lastTm"
    }

    /* 확장 레이아웃 content description 설정 */
    private fun setContentDescription(holder: BusListViewHolder, position: Int) {
        holder.binding.ivBusListItemExpand.contentDescription = when(data[position].isExpanded) {
            true -> {
                "${data[position].rtNm}번 버스 상세정보 닫기"
            }
            false -> {
                "${data[position].rtNm}번 버스 상세정보 보기"
            }
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

    override fun getItemCount(): Int = data.size

    /* 데이터 갱신 */
    fun updateData(apiData: List<BusListData.MsgBody.BusList>) {
        data = apiData
        notifyDataSetChanged()
    }

    /* ViewHolder */
    inner class BusListViewHolder(@NonNull val binding: RvBusListItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

@BindingAdapter("color")
fun setColor(civ: CircleImageView, routeType: Int) {
    when (routeType) {
        6 -> {
            civ.contentDescription = "광역버스"
            R.drawable.red
        }
        3 -> {
            civ.contentDescription = "간선버스"
            R.drawable.blue
        }
        4 -> {
            civ.contentDescription = "지선버스"
            R.drawable.green
        }
        5 -> {
            civ.contentDescription = "순환버스"
            R.drawable.yellow
        }
        2 -> {
            civ.contentDescription = "마을버스"
            R.drawable.white
        }
        else -> {
            civ.contentDescription = "기타버스"
            R.drawable.light_gray
        }
    }.apply {
        civ.setImageResource(this)
    }
}


