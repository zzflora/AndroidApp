package com.example.loginapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter(private val messages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val friendMessageLayout: LinearLayout = itemView.findViewById(R.id.friendMessageLayout)
        val friendAvatar: ImageView = itemView.findViewById(R.id.friendAvatar)
        val friendMessageText: TextView = itemView.findViewById(R.id.friendMessageText)

        val userMessageLayout: LinearLayout = itemView.findViewById(R.id.userMessageLayout)
        val userMessageText: TextView = itemView.findViewById(R.id.userMessageText)
        val userAvatar: ImageView = itemView.findViewById(R.id.userAvatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]

        if (message.isUser) {
            // 用户消息
            holder.userMessageLayout.visibility = View.VISIBLE
            holder.friendMessageLayout.visibility = View.GONE
            holder.userMessageText.text = message.text
        } else {
            // 好友消息
            holder.userMessageLayout.visibility = View.GONE
            holder.friendMessageLayout.visibility = View.VISIBLE
            holder.friendMessageText.text = message.text
        }
    }

    override fun getItemCount(): Int = messages.size
}