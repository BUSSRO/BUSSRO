package com.youreye.bussro.model.db.entity

import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.Format
import java.text.SimpleDateFormat
import java.util.*

/**
 * [History]
 * history database model class
 *
 * @param date 검색일자
 * @param rtNm 버스번호
 * @param arsId 정류소고유번호
 * @param stationNm 정류소명
 * @param scrap 즐겨찾기 여부
 */

@Entity(tableName = "history")
data class History(
    @PrimaryKey
    var date: String,

    @ColumnInfo(name = "rtNm")
    var rtNm: String,

    @ColumnInfo(name = "arsId")
    var arsId: String,

    @ColumnInfo(name = "stationNm")
    var stationNm: String,

    @ColumnInfo(name = "scrap")
    val scrap: Boolean
)

/* DataBinding_정류장명 */
@BindingAdapter("stationNm")
fun setStationNm(txt: TextView, stationNm: String) {
    txt.text = stationNm
}

/* DataBinding_즐겨찾기 여부 */
@BindingAdapter("scrap")
fun setScrap(tb: ToggleButton, scrap: Boolean) {
    tb.isChecked = scrap
}

/*
/* DataBinding_검색일 */
@BindingAdapter("date")
fun setDate(txt: TextView, date: Date) {
    val dateFormat = SimpleDateFormat("yy.MM.dd hh:mm", Locale.getDefault())
    txt.text = dateFormat.format(date)
}

/* DataBinding_버스번호 */
@BindingAdapter("rtNm")
fun setRtNm(txt: TextView, rtNm: String) {
    txt.text = rtNm
}
 */