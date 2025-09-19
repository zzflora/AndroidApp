package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var backButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 初始化数据库帮助类
        dbHelper = DatabaseHelper(this)

        // 初始化视图
        initViews()

        // 设置注册按钮点击事件
        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "密码不一致", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 添加用户到数据库
            val result = dbHelper.addUser(username, password)
            if (result > 0) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show()

                // 可选：自动填充登录界面的用户名
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                finish() // 结束注册界面
            } else {
                Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show()
            }
        }

        // 设置返回按钮点击事件 - 返回到登录界面
        backButton.setOnClickListener {
            // 创建一个新的Intent返回到MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // 结束当前Activity
        }
    }

    private fun initViews() {
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        backButton = findViewById(R.id.backButton)
    }

    // 重写返回键行为
    override fun onBackPressed() {
        // 返回到登录界面
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}