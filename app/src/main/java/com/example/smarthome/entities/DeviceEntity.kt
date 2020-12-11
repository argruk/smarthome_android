package com.example.smarthome.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Device(
    @PrimaryKey(autoGenerate = true) val id: String?,
    val title: String?,
    val room_id: String?,
    val state: Boolean?,
    val pinNumber: String?
)