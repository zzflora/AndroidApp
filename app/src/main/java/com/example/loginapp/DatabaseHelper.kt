package com.example.loginapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.text.insert

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "SpiderVerse.db"
        private const val DATABASE_VERSION = 1

        // 用户表
        const val TABLE_USERS = "users"
        const val COLUMN_USER_ID = "user_id"
        const val COLUMN_USERNAME = "username"
        const val COLUMN_PASSWORD = "password"

        // 好友表
        const val TABLE_FRIENDS = "friends"
        const val COLUMN_FRIEND_ID = "friend_id"
        const val COLUMN_FRIEND_NAME = "friend_name"
        const val COLUMN_FRIEND_AVATAR = "friend_avatar"

        // 消息表
        const val TABLE_MESSAGES = "messages"
        const val COLUMN_MESSAGE_ID = "message_id"
        const val COLUMN_SENDER_ID = "sender_id"
        const val COLUMN_RECEIVER_ID = "receiver_id"
        const val COLUMN_MESSAGE_TEXT = "message_text"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_IS_USER = "is_user"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 创建用户表
        val createUserTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()

        // 创建好友表
        val createFriendTable = """
            CREATE TABLE $TABLE_FRIENDS (
                $COLUMN_FRIEND_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_FRIEND_NAME TEXT NOT NULL,
                $COLUMN_FRIEND_AVATAR INTEGER NOT NULL
            )
        """.trimIndent()

        // 创建消息表
        val createMessageTable = """
            CREATE TABLE $TABLE_MESSAGES (
                $COLUMN_MESSAGE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SENDER_ID INTEGER NOT NULL,
                $COLUMN_RECEIVER_ID INTEGER NOT NULL,
                $COLUMN_MESSAGE_TEXT TEXT NOT NULL,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                $COLUMN_IS_USER INTEGER NOT NULL
            )
        """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createFriendTable)
        db.execSQL(createMessageTable)

        // 插入预设好友（蜘蛛侠角色）
        insertDefaultFriends(db)
        // 插入预设消息
        insertDefaultMessages(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FRIENDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        onCreate(db)
    }

    // 插入默认好友
    private fun insertDefaultFriends(db: SQLiteDatabase) {
        val friends = listOf(
            Pair("迈尔斯·莫拉莱斯", R.drawable.miles),
            Pair("彼得·帕克", R.drawable.peter_parker),
            Pair("格温·斯泰西", R.drawable.gwen),
            Pair("暗影蜘蛛侠", R.drawable.noir),
            Pair("潘妮·帕克", R.drawable.peni),
            Pair("蜘蛛侠2099", R.drawable.miguel)
        )

        friends.forEach { (name, avatar) ->
            val values = ContentValues().apply {
                put(COLUMN_FRIEND_NAME, name)
                put(COLUMN_FRIEND_AVATAR, avatar)
            }
            db.insert(TABLE_FRIENDS, null, values)
        }
    }

    // 插入默认消息
    private fun insertDefaultMessages(db: SQLiteDatabase) {
        // 这里可以预设一些初始消息
        val messages = listOf(
            // 每个消息是一个四元组（senderId, receiverId, text, isUser）
            Triple(2, 1, "嘿！新来的？我是迈尔斯。"),
            Triple(2, 1, "这个宇宙需要更多的蜘蛛侠，你准备好加入了吗？"),
            Triple(3, 1, "嗨！我是格温。喜欢鼓点吗？"),
            Triple(3, 1, "有时候我觉得音乐是连接所有宇宙的线索。")
        )

        messages.forEach { (senderId, receiverId, text) ->
            val values = ContentValues().apply {
                put(COLUMN_SENDER_ID, senderId)
                put(COLUMN_RECEIVER_ID, receiverId)
                put(COLUMN_MESSAGE_TEXT, text)
                put(COLUMN_IS_USER, 0) // 0表示不是用户发送的消息
            }
            db.insert(TABLE_MESSAGES, null, values)
        }
    }

    // 添加用户
    fun addUser(username: String, password: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, values)
    }

    // 检查用户是否存在
    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val selection = "$COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?"
        val selectionArgs = arrayOf(username, password)
        val cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null)
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    // 获取所有好友
    fun getAllFriends(): List<Friend> {
        val friends = mutableListOf<Friend>()
        val db = readableDatabase
        val cursor = db.query(TABLE_FRIENDS, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_FRIEND_ID))
                val name = getString(getColumnIndexOrThrow(COLUMN_FRIEND_NAME))
                val avatar = getInt(getColumnIndexOrThrow(COLUMN_FRIEND_AVATAR))
                friends.add(Friend(id, name, avatar))
            }
        }
        cursor.close()
        return friends
    }

    // 获取与特定好友的聊天记录
    fun getMessagesWithFriend(friendId: Int): List<Message> {
        val messages = mutableListOf<Message>()
        val db = readableDatabase
        val selection = "($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?) OR ($COLUMN_SENDER_ID = ? AND $COLUMN_RECEIVER_ID = ?)"
        val selectionArgs = arrayOf("1", friendId.toString(), friendId.toString(), "1")
        val cursor = db.query(TABLE_MESSAGES, null, selection, selectionArgs, null, null, COLUMN_TIMESTAMP)

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(COLUMN_MESSAGE_ID))
                val senderId = getInt(getColumnIndexOrThrow(COLUMN_SENDER_ID))
                val receiverId = getInt(getColumnIndexOrThrow(COLUMN_RECEIVER_ID))
                val text = getString(getColumnIndexOrThrow(COLUMN_MESSAGE_TEXT))
                val timestamp = getString(getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                val isUser = getInt(getColumnIndexOrThrow(COLUMN_IS_USER)) == 1
                messages.add(Message(id, senderId, receiverId, text, timestamp, isUser))
            }
        }
        cursor.close()
        return messages
    }

    // 添加新消息
    fun addMessage(senderId: Int, receiverId: Int, text: String, isUser: Boolean): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_SENDER_ID, senderId)
            put(COLUMN_RECEIVER_ID, receiverId)
            put(COLUMN_MESSAGE_TEXT, text)
            put(COLUMN_IS_USER, if (isUser) 1 else 0)
        }
        return db.insert(TABLE_MESSAGES, null, values)
    }

    // 添加好友的自动回复
    fun addFriendResponse(friendId: Int, message: String) {
        addMessage(friendId, 1, message, false)
    }
    // 在 DatabaseHelper 类中添加以下方法

    // 添加辅助方法插入消息
    private fun insertMessages(db: SQLiteDatabase, messages: List<Array<Any>>) {
        messages.forEach { msg ->
            val values = ContentValues().apply {
                put(COLUMN_SENDER_ID, msg[0] as Int)
                put(COLUMN_RECEIVER_ID, msg[1] as Int)
                put(COLUMN_MESSAGE_TEXT, msg[2] as String)
                put(COLUMN_IS_USER, msg[3] as Int)
            }
            db.insert(TABLE_MESSAGES, null, values)
        }
    }

    // 在DatabaseHelper类中添加获取可写数据库的方法
    fun getWritableDb(): SQLiteDatabase {
        return this.writableDatabase
    }

    // 添加初始化用户对话的方法
    fun initializeUserConversation(userId: Int) {
        val db = this.writableDatabase
        // 清空该用户的所有消息
        db.delete(TABLE_MESSAGES, "$COLUMN_RECEIVER_ID = ?", arrayOf(userId.toString()))

        // 插入初始消息
        val messages = listOf(
            arrayOf(2, userId, "嘿！新来的？我是迈尔斯。", 0),
            arrayOf(2, userId, "这个宇宙需要更多的蜘蛛侠，你准备好加入了吗？", 0)
        )

        messages.forEach { msg ->
            val values = ContentValues().apply {
                put(COLUMN_SENDER_ID, msg[0] as Int)
                put(COLUMN_RECEIVER_ID, msg[1] as Int)
                put(COLUMN_MESSAGE_TEXT, msg[2] as String)
                put(COLUMN_IS_USER, msg[3] as Int)
            }
            db.insert(TABLE_MESSAGES, null, values)
        }
    }
}


// 数据类
data class Friend(val id: Int, val name: String, val avatar: Int)
data class Message(val id: Int, val senderId: Int, val receiverId: Int, val text: String, val timestamp: String, val isUser: Boolean)