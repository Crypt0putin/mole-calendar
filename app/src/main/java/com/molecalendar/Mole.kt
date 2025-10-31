package com.molecalendar

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moles")
data class Mole(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val location: String,
    val description: String,
    val size: Float,
    val color: String,
    val addedDate: Long,
    val lastCheckedDate: Long,
    val nextCheckDate: Long,
    val photoPath: String?,
    val reminderEnabled: Boolean = false,
    val reminderIntervalMonths: Int = 3
)
