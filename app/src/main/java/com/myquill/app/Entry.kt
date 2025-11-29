package com.myquill.app

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val body: String,
    val createdAt: Long,
    val imageUri: String?,
    val lat: Double?,
    val lng: Double?,
    val address: String? = null,
    val companions: String? = null
)
