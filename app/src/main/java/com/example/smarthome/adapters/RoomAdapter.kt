package com.example.smarthome.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.smarthome.R
import com.example.smarthome.activities.DetailActivity
import com.example.smarthome.entities.RoomEntity
import kotlinx.android.synthetic.main.room_list_item.view.*


class RoomAdapter(var rooms: List<RoomEntity>, var context: Context): RecyclerView.Adapter<RoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_list_item, parent, false)
        val viewHolder = RoomViewHolder(view)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return rooms.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val data = rooms[position]
        holder.item.room_title.text = data.title
        val id: Int = context.resources.getIdentifier(data.icon.toString(), "drawable", context.packageName)
        holder.item.room_image.setImageResource(id)

        holder.item.room_title.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("roomId", data.id)
            context.startActivity(intent)
        }
    }

}