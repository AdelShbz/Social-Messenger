package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityChatListBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ChatListActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatListBinding
    private var gson: Gson = Gson()
    private var chatList = mutableListOf<ChatList>()
    private var roomList = mutableListOf<Room>()
    private lateinit var adapter: ChatListAdapter
    private val apiService = RetrofitClient.instance
    private var socket: Socket? = null

    lateinit var token:String
    lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        token = intent.getStringExtra("TOKEN").toString()
        username = intent.getStringExtra("USERNAME").toString()
        setupRecyclerView()

        apiService.getChatList("Bearer $token").enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val jsonString = response.body()?.string()
                    val type = object : TypeToken<MutableList<Room>>() {}.type
                    roomList = gson.fromJson(jsonString, type)

                    var chatName: String
                    var lastMessage: String
                    roomList.forEach { room ->
                        if (room.members[0] == username) chatName = room.members[1]
                        else chatName = room.members[0]
                        if (room.chats.size == 0) lastMessage = "There is no Message"
                        else lastMessage = room.chats[room.chats.size - 1].text

                        val onechatlist = ChatList(chatName, lastMessage)
                        chatList.add(onechatlist)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
        })

        socket = IO.socket(Our_Url().our_url)
        socket?.connect()
        socket?.on(CHAT_KEYS.NEW_CHAT) {args ->
            val room = Gson().fromJson(args[0].toString(), Room::class.java) as Room
            var lastMessage: String
            if (room.members[0] == username) {
                if(room.chats.size == 0) lastMessage = "There is no Message"
                else lastMessage = room.chats[room.chats.size - 1].text
                val chatlist = ChatList(room.members[1],lastMessage)
                runOnUiThread {
                    chatList.add(chatlist)
                    adapter.notifyDataSetChanged()
                }
            } else if (room.members[1] == username) {
                if(room.chats.size == 0) lastMessage = "There is no Message"
                else lastMessage = room.chats[room.chats.size - 1].text
                val chatlist = ChatList(room.members[0],lastMessage)
                runOnUiThread {
                    chatList.add(chatlist)
                    adapter.notifyDataSetChanged()
                }
            }
        }

        socket?.on(CHAT_KEYS.PRIVATE_MESSAGE) {args ->
            val message = args[0]
            val toUsername = args[1] // convert message variable to chat data class.
            val chatMessage = Gson().fromJson(message.toString(), Chat::class.java) as Chat
            if(chatMessage.username == username){
                chatList.forEach { oneChatList ->
                    if(oneChatList.chatName == toUsername){
                        oneChatList.lastMessage = chatMessage.text
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            if(toUsername == username) {
                chatList.forEach { oneChatList ->
                    if(oneChatList.chatName == chatMessage.username){
                        oneChatList.lastMessage = chatMessage.text
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }

        binding.buttonAdd.setOnClickListener {
            val intentToContactlist = Intent(this@ChatListActivity, ContactListActivity::class.java)
            intentToContactlist.putExtra("TOKEN", token)
            intentToContactlist.putExtra("USERNAME", username)
            startActivity(intentToContactlist)
        }

    }

    private fun setupRecyclerView() {
        adapter = ChatListAdapter(chatList, this@ChatListActivity, username)
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = adapter
    }

    private object CHAT_KEYS {
        const val NEW_CHAT = "new_chat"
        const val PRIVATE_MESSAGE = "private_message"
    }
}