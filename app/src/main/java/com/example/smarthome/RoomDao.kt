package com.example.smarthome

import androidx.room.*


@Dao
interface RoomDao {
    @Query("SELECT * FROM RoomEntity")
    fun getAll(): List<RoomEntity>

    @Query("SELECT * FROM RoomEntity WHERE id IN (:room_id)")
    fun loadAllByIds(room_id: IntArray): List<RoomEntity>


    @Insert
    fun insertAll(rooms: Array<RoomEntity>)

    @Delete
    fun delete(room: RoomEntity)
}