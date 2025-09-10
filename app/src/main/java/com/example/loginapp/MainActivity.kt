package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var avatarRadioGroup: RadioGroup
    private lateinit var avatarImageView: ImageView
    private lateinit var loginButton: Button

    private val avatarResources = intArrayOf(
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        initViews()

        // 设置头像选择监听器
        avatarRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val selectedIndex = when (checkedId) {
                R.id.avatar1 -> 0
                R.id.avatar2 -> 1
                R.id.avatar3 -> 2
                else -> 0
            }
            avatarImageView.setImageResource(avatarResources[selectedIndex])
        }

        // 设置登录按钮点击事件
        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                // 简单的验证
                return@setOnClickListener
            }

            // 获取选中的头像索引
            val selectedAvatarIndex = when (avatarRadioGroup.checkedRadioButtonId) {
                R.id.avatar1 -> 0
                R.id.avatar2 -> 1
                R.id.avatar3 -> 2
                else -> 0
            }

            // 启动HomeActivity并传递数据
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("username", username)
            intent.putExtra("avatarResource", avatarResources[selectedAvatarIndex])
            startActivity(intent)
        }
    }

    private fun initViews() {
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        avatarRadioGroup = findViewById(R.id.avatarRadioGroup)
        avatarImageView = findViewById(R.id.avatarImageView)
        loginButton = findViewById(R.id.loginButton)
    }
}