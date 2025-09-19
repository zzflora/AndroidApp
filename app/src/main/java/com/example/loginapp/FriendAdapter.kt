package com.example.loginapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendAdapter(
    private val friends: List<Friend>,
    private val onItemClick: (Friend) -> Unit
) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.friendAvatar)
        val name: TextView = itemView.findViewById(R.id.friendName)
        val lastMessage: TextView = itemView.findViewById(R.id.lastMessage)
        val timestamp: TextView = itemView.findViewById(R.id.timestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]

        holder.avatar.setImageResource(friend.avatar)
        holder.name.text = friend.name
        holder.lastMessage.text = "点击开始聊天"
        holder.timestamp.text = ""

        holder.itemView.setOnClickListener {
            onItemClick(friend)
        }
    }

    override fun getItemCount(): Int = friends.size
}