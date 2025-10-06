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
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        viewModel.newText.observe(this) { newText ->
            val chat  = Chat(newText.toString())
            val jsonChat = Gson().toJson(chat, Chat::class.java)
            socket?.emit(CHAT_KEYS.NEW_MESSAGE, jsonChat)
        }
        binding.btnSend.setOnClickListener {
            val message = binding.etText.text.toString()
            if (message.isEmpty()) return@setOnClickListener
            viewModel.setNewText(message)
            binding.etText.setText("")
        }

        socket?.on(CHAT_KEYS.BROADCAST) {args ->
            val data = args[0]
            val chat = Gson().fromJson(data.toString(), Chat::class.java) as Chat
            runOnUiThread {
                chatList.add(chat)
                adapter.notifyItemInserted(chatList.size - 1)
                binding.recyclerView.scrollToPosition(chatList.size - 1)
                Log.d("DATADEBUG", "$chatList")
            }
        }

    }

    private object CHAT_KEYS {
        const val NEW_MESSAGE = "new_message"
        const val BROADCAST = "broadcast"
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