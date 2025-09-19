package com.example.loginapp

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DataVisualizationActivity : AppCompatActivity() {
    private lateinit var barChartContainer: LinearLayout
    private lateinit var dataSummaryTextView: TextView
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_visualization)

        // 初始化数据库帮助类
        dbHelper = DatabaseHelper(this)

        // 初始化视图
        initViews()

        // 加载数据并显示图表
        loadDataAndDisplayCharts()
    }

    private fun initViews() {
        barChartContainer = findViewById(R.id.barChartContainer)
        dataSummaryTextView = findViewById(R.id.dataSummaryTextView)
    }

    private fun loadDataAndDisplayCharts() {
        // 获取用户数量
        val userCount = getUserCount()

        // 获取好友数量
        val friendCount = getFriendCount()

        // 获取消息数量
        val messageCount = getMessageCount()

        // 获取每个好友的消息数量
        val messagesPerFriend = getMessagesPerFriend()

        // 显示数据摘要
        showDataSummary(userCount, friendCount, messageCount)

        // 显示柱状图
        displayBarChart(messagesPerFriend)
    }

    private fun getUserCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun getFriendCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_FRIENDS}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun getMessageCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_MESSAGES}", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun getMessagesPerFriend(): Map<String, Int> {
        val messagesPerFriend = mutableMapOf<String, Int>()
        val db = dbHelper.readableDatabase

        // 查询每个好友的消息数量
        val query = """
            SELECT f.${DatabaseHelper.COLUMN_FRIEND_NAME}, COUNT(m.${DatabaseHelper.COLUMN_MESSAGE_ID}) as message_count
            FROM ${DatabaseHelper.TABLE_FRIENDS} f
            LEFT JOIN ${DatabaseHelper.TABLE_MESSAGES} m ON f.${DatabaseHelper.COLUMN_FRIEND_ID} = m.${DatabaseHelper.COLUMN_SENDER_ID}
            GROUP BY f.${DatabaseHelper.COLUMN_FRIEND_ID}
            ORDER BY message_count DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val friendName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FRIEND_NAME))
            val messageCount = cursor.getInt(cursor.getColumnIndexOrThrow("message_count"))
            messagesPerFriend[friendName] = messageCount
        }
        cursor.close()

        return messagesPerFriend
    }

    private fun showDataSummary(userCount: Int, friendCount: Int, messageCount: Int) {
        val summary = """
            用户数量: $userCount
            好友数量: $friendCount
            消息总数: $messageCount
            平均每个好友的消息数: ${if (friendCount > 0) "%.2f".format(messageCount.toFloat() / friendCount) else "0"}
        """.trimIndent()

        dataSummaryTextView.text = summary
    }

    private fun displayBarChart(messagesPerFriend: Map<String, Int>) {
        // 清空容器
        barChartContainer.removeAllViews()

        // 获取最大值用于比例计算
        val maxValue = messagesPerFriend.values.maxOrNull() ?: 1

        // 为每个好友创建柱状图
        messagesPerFriend.forEach { (friendName, messageCount) ->
            // 创建文本视图显示好友名称
            val nameTextView = TextView(this)
            nameTextView.text = friendName
            nameTextView.setTextColor(Color.WHITE)
            nameTextView.setPadding(0, 8, 0, 4)

            // 创建自定义视图显示柱状图
            val barView = object : View(this) {
                override fun onDraw(canvas: Canvas) {
                    super.onDraw(canvas)

                    val paint = Paint()
                    paint.color = when (friendName) {
                        "迈尔斯·莫拉莱斯" -> Color.parseColor("#E23636") // 蜘蛛红
                        "格温·斯泰西" -> Color.parseColor("#2B78C5") // 蜘蛛蓝
                        "彼得·帕克" -> Color.parseColor("#FFA500") // 橙色
                        else -> Color.parseColor("#800080") // 紫色
                    }

                    val barWidth = width.toFloat()
                    val barHeight = (height * (messageCount.toFloat() / maxValue.toFloat())).toFloat()

                    val rect = RectF(0f, height - barHeight, barWidth, height.toFloat())
                    canvas.drawRect(rect, paint)

                    // 绘制数值文本
                    val textPaint = Paint()
                    textPaint.color = Color.WHITE
                    textPaint.textSize = 24f
                    textPaint.textAlign = Paint.Align.CENTER

                    canvas.drawText(
                        messageCount.toString(),
                        barWidth / 2,
                        height - barHeight - 10,
                        textPaint
                    )
                }
            }

            // 设置柱状图视图的布局参数
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                150 // 固定高度
            )
            layoutParams.setMargins(0, 0, 0, 16)
            barView.layoutParams = layoutParams

            // 添加到容器
            barChartContainer.addView(nameTextView)
            barChartContainer.addView(barView)
        }
    }
}