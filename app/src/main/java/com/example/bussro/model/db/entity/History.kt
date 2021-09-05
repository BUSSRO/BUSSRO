package com.example.bussro.model.db.entity

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
    var arsId: String?,

    @ColumnInfo(name = "stationNm")
    var stationNm: String?
)
