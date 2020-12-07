package com.example.smarthome.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String?,
    val icon: String?
)