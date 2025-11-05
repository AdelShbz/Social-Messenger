package com.example.socialmessenger

import android.os.Bundle
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
        self_username = intent.getStringExtra("SELF_USERNAME").toString()
        other_username = intent.getStringExtra("OTHER_USERNAME").toString()
        socket = IO.socket(SOCKET_URL)
        socket?.connect()
        setupRecyclerView()
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        members.add(self_username)
        members.add(other_username)
        chats.add(Chat("hello everyone", "adel"))
        chats.add(Chat("hello too", "ali"))
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
//            Log.d("test", room.toString())
            runOnUiThread {
                chatList.addAll(room.chats)
                adapter.notifyDataSetChanged()
            }
        }
//        socket?.on(CHAT_KEYS.BROADCAST) {args ->
//            val data = args[0]
//            val chat = Gson().fromJson(data.toString(), Chat::class.java) as Chat
//            runOnUiThread {
//                chatList.add(chat)
//                adapter.notifyItemInserted(chatList.size - 1)
//                binding.recyclerView.scrollToPosition(chatList.size - 1)
//                Log.d("DATADEBUG", "$chatList")
//            }
//        }

    }

    private object CHAT_KEYS {
//        const val NEW_MESSAGE = "new_message"
        const val GROUP_MESSAGE = "group_message"
        const val PRIVATE_MESSAGE = "private_message"
//        const val BROADCAST = "broadcast"
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