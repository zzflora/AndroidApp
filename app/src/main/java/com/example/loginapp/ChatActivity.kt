package com.example.loginapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {
    private lateinit var friendAvatar: ImageView
    private lateinit var friendName: TextView
    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var replyOptionsLayout: LinearLayout
    private lateinit var replyOption1: Button
    private lateinit var replyOption2: Button
    private lateinit var replyOption3: Button
    private lateinit var resetButton: Button

    private lateinit var dbHelper: DatabaseHelper
    private var friendId: Int = 0
    private var friendNameStr: String = ""

    // 对话流程和回复选项
    private val conversationFlows = mapOf(
        // 迈尔斯的对话流程
        2 to listOf(
            "嘿！新来的？我是迈尔斯。",
            "这个宇宙需要更多的蜘蛛侠，你准备好加入了吗？",
            "酷！我最喜欢涂鸦了，你会画画吗？",
            "有时候我觉得自己还不够好，但格温说每个蜘蛛侠都这么想。",
            "不管怎样，很高兴认识你！我们以后就是队友了！"
        ),
        // 格温的对话流程
        3 to listOf(
            "嗨！我是格温。喜欢鼓点吗？",
            "有时候我觉得音乐是连接所有宇宙的线索。",
            "你知道吗？在另一个宇宙，我是个很棒的鼓手。",
            "生活就像节奏，有时候快，有时候慢，但总有它的韵律。",
            "保持节奏，朋友！我们很快会再见的。"
        )
        // 可以继续添加其他角色的对话流程
    )

    // 回复选项
    private val replyOptions = mapOf(
        2 to listOf(
            listOf("当然准备好了！", "我有点紧张...", "我需要更多训练"),
            listOf("我会一点！", "我不太会画画", "我更擅长音乐"),
            listOf("你已经很棒了！", "我也有同感", "格温说得对"),
            listOf("我也很高兴认识你！", "期待一起战斗", "保持联系！")
        ),
        3 to listOf(
            listOf("超喜欢！", "一般般", "我更偏好安静"),
            listOf("这想法真有趣", "我没想过这个", "音乐确实很神奇"),
            listOf("真酷！", "我想听听", "你一定会很厉害"),
            listOf("说得真好", "我需要找到自己的节奏", "我会记住的")
        )
    )

    // 角色对不同选项的回复
    private val characterResponses = mapOf(
        2 to mapOf(
            "当然准备好了！" to "太棒了！期待与你并肩作战！",
            "我有点紧张..." to "别担心，每个蜘蛛侠开始都这样！",
            "我需要更多训练" to "好吧，我们一起来训练！",
            "我会一点！" to "太好了！我们可以一起创作街头艺术！",
            "我不太会画画" to "没关系，蜘蛛侠的能力不止一种！",
            "我更擅长音乐" to "音乐也很酷！格温会喜欢你的！",
            "你已经很棒了！" to "谢谢！这让我更有信心了！",
            "我也有同感" to "对吧？有时候压力真的很大...",
            "格温说得对" to "她总是知道该说什么。",
            "我也很高兴认识你！" to "太好了！欢迎加入蜘蛛侠联盟！",
            "期待一起战斗" to "我也是！让我们守护这个城市！",
            "保持联系！" to "当然！有事随时叫我！"
        ),
        3 to mapOf(
            "超喜欢！" to "太好了！音乐是宇宙的通用语言！",
            "一般般" to "没关系，每个人都有自己的喜好。",
            "我更偏好安静" to "安静也有它的美，我理解。",
            "这想法真有趣" to "对吧？我觉得节奏连接了一切。",
            "我没想过这个" to "有时候换个角度思考很有趣。",
            "音乐确实很神奇" to "它能让不同世界的人产生共鸣。",
            "真酷！" to "想听我打鼓吗？虽然这里没有鼓...",
            "我想听听" to "有机会一定让你听听！",
            "你一定会很厉害" to "谢谢！在另一个宇宙我确实不错！",
            "说得真好" to "谢谢，这是我在各个宇宙旅行的体会。",
            "我需要找到自己的节奏" to "每个人都有自己的节奏，你会找到的！",
            "我会记住的" to "太好了！保持你的节奏，朋友！"
        )
    )

    private var currentStep = 0
    private var conversationActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // 获取传递过来的好友信息
        friendId = intent.getIntExtra("friend_id", 0)
        friendNameStr = intent.getStringExtra("friend_name") ?: "蜘蛛侠"

        // 初始化数据库帮助类
        dbHelper = DatabaseHelper(this)

        // 初始化视图
        initViews()

        // 设置重置按钮点击事件
        resetButton.setOnClickListener { resetConversation() }

        // 设置好友信息
        friendName.text = friendNameStr

        // 加载聊天记录
        loadMessages()

        // 如果没有消息或对话未完成，检查是否需要继续对话
        val messages = dbHelper.getMessagesWithFriend(friendId)
        if (messages.isEmpty() || conversationActive) {
            continueConversation()
        }

        // 设置发送按钮点击事件
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString()
            if (message.isNotBlank()) {
                sendMessage(message)
                messageEditText.text.clear()

                // 如果是预设对话流程中，触发好友回复
                if (conversationActive && currentStep < (conversationFlows[friendId]?.size ?: 0)) {
                    // 延迟一会再发送回复，让用户体验更自然
                    messagesRecyclerView.postDelayed({
                        sendCharacterResponse()
                    }, 1000)
                }
            }
        }

        // 设置回复选项点击事件
        replyOption1.setOnClickListener { onReplyOptionSelected(0) }
        replyOption2.setOnClickListener { onReplyOptionSelected(1) }
        replyOption3.setOnClickListener { onReplyOptionSelected(2) }
    }

    private fun initViews() {
        friendAvatar = findViewById(R.id.friendAvatar)
        friendName = findViewById(R.id.friendName)
        messagesRecyclerView = findViewById(R.id.messagesRecyclerView)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        replyOptionsLayout = findViewById(R.id.replyOptionsLayout)
        replyOption1 = findViewById(R.id.replyOption1)
        replyOption2 = findViewById(R.id.replyOption2)
        replyOption3 = findViewById(R.id.replyOption3)
        resetButton = findViewById(R.id.resetButton) // 初始化重置按钮

        // 设置RecyclerView
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadMessages() {
        val messages = dbHelper.getMessagesWithFriend(friendId)
        val adapter = MessageAdapter(messages)
        messagesRecyclerView.adapter = adapter
        messagesRecyclerView.scrollToPosition(messages.size - 1)
    }

    private fun sendMessage(text: String) {
        dbHelper.addMessage(1, friendId, text, true)
        loadMessages()
    }

    private fun sendCharacterResponse() {
        val response = conversationFlows[friendId]?.getOrNull(currentStep) ?: return
        dbHelper.addFriendResponse(friendId, response)
        currentStep++
        loadMessages()

        // 检查是否应该显示回复选项
        if (currentStep < (conversationFlows[friendId]?.size ?: 0)) {
            showReplyOptions()
        } else {
            // 对话结束
            conversationActive = false
        }
    }

    private fun continueConversation() {
        val messages = dbHelper.getMessagesWithFriend(friendId)
        val userMessages = messages.filter { it.isUser }

        // 找出当前的对话步骤
        currentStep = userMessages.size

        // 如果对话未完成，继续
        if (currentStep < (conversationFlows[friendId]?.size ?: 0)) {
            // 检查是否需要显示回复选项
            if (currentStep > 0) {
                showReplyOptions()
            } else {
                // 开始对话
                sendCharacterResponse()
            }
        } else {
            conversationActive = false
        }
    }

    private fun showReplyOptions() {
        val options = replyOptions[friendId]?.getOrNull(currentStep - 1) ?: return

        // 设置选项文本
        replyOption1.text = options.getOrNull(0) ?: ""
        replyOption2.text = options.getOrNull(1) ?: ""
        replyOption3.text = options.getOrNull(2) ?: ""

        // 显示选项布局，隐藏输入框
        replyOptionsLayout.visibility = View.VISIBLE
        messageEditText.visibility = View.GONE
        sendButton.visibility = View.GONE
    }

    private fun hideReplyOptions() {
        replyOptionsLayout.visibility = View.GONE
        messageEditText.visibility = View.VISIBLE
        sendButton.visibility = View.VISIBLE
    }

    private fun onReplyOptionSelected(optionIndex: Int) {
        val options = replyOptions[friendId]?.getOrNull(currentStep - 1) ?: return
        val selectedOption = options.getOrNull(optionIndex) ?: return

        // 发送用户选择的选项
        sendMessage(selectedOption)
        hideReplyOptions()

        // 获取角色对选项的回复
        val response = characterResponses[friendId]?.get(selectedOption) ?: "我不知道该怎么回应..."

        // 延迟一会再发送角色回复
        messagesRecyclerView.postDelayed({
            dbHelper.addFriendResponse(friendId, response)
            loadMessages()

            // 继续对话
            if (currentStep < (conversationFlows[friendId]?.size ?: 0)) {
                messagesRecyclerView.postDelayed({
                    sendCharacterResponse()
                }, 1000)
            } else {
                conversationActive = false
            }
        }, 1000)
    }

    private fun resetConversation() {
        // 清空与当前好友的聊天记录
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_MESSAGES,
            "(${DatabaseHelper.COLUMN_SENDER_ID} = ? AND ${DatabaseHelper.COLUMN_RECEIVER_ID} = ?) OR (${DatabaseHelper.COLUMN_SENDER_ID} = ? AND ${DatabaseHelper.COLUMN_RECEIVER_ID} = ?)",
            arrayOf("1", friendId.toString(), friendId.toString(), "1")
        )

        // 重置对话状态
        currentStep = 0
        conversationActive = true

        // 重新加载消息
        loadMessages()

        // 重新开始对话
        continueConversation()
    }
}