package com.example.loginapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ConversationListActivity : AppCompatActivity() {
    private lateinit var friendsRecyclerView: RecyclerView
    private lateinit var visualizationButton: ImageButton
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversation_list)

        // 初始化数据库帮助类
        dbHelper = DatabaseHelper(this)

        // 初始化视图
        initViews()

        // 获取好友列表
        val friends = dbHelper.getAllFriends()

        // 设置RecyclerView
        val adapter = FriendAdapter(friends) { friend ->
            // 点击好友，跳转到聊天界面
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("friend_id", friend.id)
            intent.putExtra("friend_name", friend.name)
            startActivity(intent)
        }

        friendsRecyclerView.layoutManager = LinearLayoutManager(this)
        friendsRecyclerView.adapter = adapter

        // 设置可视化按钮点击事件
        visualizationButton.setOnClickListener {
            val intent = Intent(this, DataVisualizationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initViews() {
        friendsRecyclerView = findViewById(R.id.friendsRecyclerView)
        visualizationButton = findViewById(R.id.visualizationButton)
    }

}
