package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var socket: Socket? = null
    lateinit var viewModel: MyViewModel
    private var gson: Gson = Gson()
    private val chatList = mutableListOf<Chat>()
    private lateinit var adapter: ChatAdapter
    private val members = mutableListOf<String>()
    private val chats = mutableListOf<Chat>()
    lateinit var self_username: String
    lateinit var other_username: String
    lateinit var ourRoom: Room
    var membersInCL = mutableListOf<ContactList>()
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
        val type = intent.getStringExtra("TYPE").toString()
        binding.tvTypeMain.text = type
        self_username = intent.getStringExtra("SELF_USERNAME").toString()
        var id = intent.getStringExtra("ID").toString()
        if (type == "group"){
            val roomName = intent.getStringExtra("ROOM_NAME").toString()
            binding.tvRoomName.text = roomName
            val membersInString = intent.getStringExtra("MEMBERS")
            val typeForConvert = object : TypeToken<MutableList<ContactList>>() {}.type
            membersInCL = gson.fromJson(membersInString, typeForConvert)
            membersInCL.forEach { memberCL -> members.add(memberCL.username) }
            val groupRoles = GroupRoles(self_username)
            val room = Room(id, type, members, chats, roomName, groupRoles)
            val jsonRoom = gson.toJson(room, Room::class.java)
            socket?.emit(CHAT_KEYS.GET_ROOM, jsonRoom)
            socket?.on(CHAT_KEYS.GET_ROOM) {args ->
                val data = args[0]
                ourRoom = Gson().fromJson(data.toString(), Room::class.java) as Room
                id = ourRoom._id
                runOnUiThread {
                    chatList.addAll(ourRoom.chats)
                    adapter.notifyDataSetChanged()
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        } else if (type == "private"){
            other_username = intent.getStringExtra("OTHER_USERNAME").toString()
            members.add(self_username)
            members.add(other_username)
            binding.tvRoomName.text = other_username
            val privateRoles = GroupRoles("")
            val room = Room(id, type, members, chats,"", privateRoles)
            val jsonRoom = gson.toJson(room, Room::class.java)
            socket?.emit(CHAT_KEYS.GET_ROOM, jsonRoom)
            socket?.on(CHAT_KEYS.GET_ROOM) {args ->
                val data = args[0]
                ourRoom = Gson().fromJson(data.toString(), Room::class.java) as Room
                id = ourRoom._id
                runOnUiThread {
                    chatList.addAll(ourRoom.chats)
                    adapter.notifyDataSetChanged()
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        }
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        viewModel.newText.observe(this) { newText ->
            val chat  = Chat(newText.toString(),self_username)
            val jsonChat = Gson().toJson(chat, Chat::class.java)
            if(type == "private"){
                val toUsername = other_username
                socket?.emit(CHAT_KEYS.PRIVATE_MESSAGE, jsonChat, toUsername)
            }else if (type == "group") {
                val groupId = id
                socket?.emit(CHAT_KEYS.GROUP_MESSAGE, jsonChat,groupId)
            }
        }
        binding.btnSend.setOnClickListener {
            val message = binding.etText.text.toString()
            if (message.isEmpty()) return@setOnClickListener
            viewModel.setNewText(message)
            binding.etText.setText("")
        }
        socket?.on(CHAT_KEYS.PRIVATE_MESSAGE) {args ->
            val message = args[0]
            val toId = args[1]
            val chatMessage = gson.fromJson(message.toString(), Chat::class.java) as Chat
            if (id == toId){
                runOnUiThread {
                    chatList.add(chatMessage)
                    adapter.notifyItemInserted(chatList.size - 1)
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        }
        socket?.on(CHAT_KEYS.GROUP_MESSAGE) {args ->
            val message = args[0]
            val groupId = args[1].toString()
            val chatMessage = gson.fromJson(message.toString(), Chat::class.java) as Chat
            if (groupId == id) {
                runOnUiThread {
                    chatList.add(chatMessage)
                    adapter.notifyItemInserted(chatList.size - 1)
                    binding.recyclerView.scrollToPosition(chatList.size - 1)
                }
            }
        }

        if (type != "private") {
            binding.cvMain.setOnClickListener {
                val intent = Intent(this@MainActivity, RoomInfoActivity::class.java)
                val ourRoomJson = gson.toJson(ourRoom)
                intent.putExtra("ROOM", ourRoomJson)
                intent.putExtra("SELF_USERNAME",self_username)
                startActivity(intent)
            }
        }

    }

    private object CHAT_KEYS {
        const val PRIVATE_MESSAGE = "private_message"
        const val GROUP_MESSAGE = "group_message"
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