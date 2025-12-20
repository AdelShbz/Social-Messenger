package com.example.socialmessenger

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityMainBinding
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var socket: Socket? = null
    lateinit var viewModel: MyViewModel
    private val chatList = mutableListOf<Chat>()
    private lateinit var adapter: ChatAdapter
    private val members = mutableListOf<String>()
    private val chats = mutableListOf<Chat>()
    lateinit var self_username: String
    lateinit var other_username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        socket = IO.socket(SOCKET_URL)
        socket?.connect()
        setupRecyclerView()
        val type = intent.getStringExtra("TYPE")
        if (type == "group"){

        } else if (type == "private"){
            self_username = intent.getStringExtra("SELF_USERNAME").toString()
            other_username = intent.getStringExtra("OTHER_USERNAME").toString()
            viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
            members.add(self_username)
            members.add(other_username)
            val room = Room("private", members, chats)
            val jsonRoom = Gson().toJson(room, Room::class.java)
            socket?.emit(CHAT_KEYS.GET_ROOM, jsonRoom)
            viewModel.newText.observe(this) { newText ->
                val chat  = Chat(newText.toString(),self_username)
                val jsonChat = Gson().toJson(chat, Chat::class.java)
                val toUsername = other_username
                if(room.type == "private"){
                    socket?.emit(CHAT_KEYS.PRIVATE_MESSAGE, jsonChat, toUsername)
                }
            }
            binding.btnSend.setOnClickListener {
                val message = binding.etText.text.toString()
                if (message.isEmpty()) return@setOnClickListener
                viewModel.setNewText(message)
                binding.etText.setText("")
            }
            socket?.on(CHAT_KEYS.GET_ROOM) {args ->
                val data = args[0]
                val room = Gson().fromJson(data.toString(), Room::class.java) as Room
                runOnUiThread {
                    chatList.addAll(room.chats)
                    adapter.notifyDataSetChanged()
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        }
        socket?.on(CHAT_KEYS.PRIVATE_MESSAGE) {args ->
            val message = args[0]
            val toUsername = args[1]// convert message variable to chat data class.
            val chatMessage = Gson().fromJson(message.toString(), Chat::class.java) as Chat
            if(
                (chatMessage.username == self_username && toUsername == other_username) ||
                (chatMessage.username == other_username && toUsername == self_username)
            ) {
                runOnUiThread {
                    chatList.add(chatMessage)
                    adapter.notifyItemInserted(chatList.size - 1)
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }

        }

    }

    private object CHAT_KEYS {
        const val PRIVATE_MESSAGE = "private_message"
        const val GET_ROOM = "get_room"
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(chatList)
        val layoutmanager = LinearLayoutManager(this)
        layoutmanager.stackFromEnd = true
        layoutmanager.reverseLayout = false
        binding.recyclerView.layoutManager = layoutmanager
        binding.recyclerView.adapter = adapter
    }
    companion object{
//        private const val SOCKET_URL = "http://localhost:PORT/"
        private val SOCKET_URL = Our_Url().our_url

    }
}