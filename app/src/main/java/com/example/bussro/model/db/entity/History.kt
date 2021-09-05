package com.example.bussro.model.db.entity

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * [History]
 * history database model class
 *
 * @param arsId 정류소고유번호
 * @param stationNm 정류소명
 */

@Entity(tableName = "history")
data class History(
    @PrimaryKey
    var arsId: String,

    @ColumnInfo(name = "stationNm")
    var stationNm: String,

    @ColumnInfo(name="date")
    var date: String
)

/* DataBinding_정류장명 */
@BindingAdapter("stationNm")
fun setStationNm(txt: TextView, stationNm: String) {
    txt.text = stationNm
}

/* DataBinding_검색일 */
@BindingAdapter("date")
fun setDate(txt: TextView, date: String) {
    txt.text = date
}