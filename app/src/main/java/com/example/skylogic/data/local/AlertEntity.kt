package com.example.skylogic.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//@Entity(tableName = "alerts")
//data class AlertEntity(
//    @PrimaryKey(autoGenerate = true)
//    val id: Int = 0,
//    val startTime: Long,
//    val endTime: Long,
//    val type: String // "notification" or "alarm"
//)
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val startTime: Long,
    val endTime: Long,

    val type: String,              // notification or alarm

    val condition: String,         // rain, snow, temp_high, temp_low, wind

    val threshold: Double? = null  // only used for temp/wind

)
