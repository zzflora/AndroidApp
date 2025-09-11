package com.example.loginapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var avatarImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var friendsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 初始化视图
        initViews()

        // 获取传递的数据
        val username = intent.getStringExtra("username") ?: "用户"
        val avatarResource = intent.getIntExtra("avatarResource", R.drawable.avatar1)

        // 显示用户信息
        usernameTextView.text = "欢迎, $username"
        avatarImageView.setImageResource(avatarResource)

        // 设置好友列表
        setupFriendsList()
    }

    private fun initViews() {
        avatarImageView = findViewById(R.id.avatarImageView)
        usernameTextView = findViewById(R.id.usernameTextView)
        friendsListView = findViewById(R.id.friendsListView)
    }

    private fun setupFriendsList() {
        val friends = arrayOf("张三", "李四", "王五", "赵六", "钱七","梁八")
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            friends
        )
        friendsListView.adapter = adapter
    }
}