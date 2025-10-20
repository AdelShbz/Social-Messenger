package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityChatListBinding
import com.google.gson.Gson

class ChatListActivity : AppCompatActivity() {
    lateinit var binding: ActivityChatListBinding
    private var gson: Gson = Gson()
    private var roomList = mutableListOf<ChatList>()
    private lateinit var adapter: ChatListAdapter
    lateinit var token:String
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
        setupRecyclerView()
//        val chatlist1 = ChatList("ali", "hello, how are you?")
//        roomList.add(chatlist1)
//        roomList.add(chatlist1)
//        roomList.add(chatlist1)
//        adapter.notifyDataSetChanged()

        binding.buttonAdd.setOnClickListener {
            val intentToContactlist = Intent(this@ChatListActivity, ContactListActivity::class.java)
            intentToContactlist.putExtra("TOKEN", token)
            startActivity(intentToContactlist)
        }

    }

    private fun setupRecyclerView() {
        adapter = ChatListAdapter(roomList)
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = adapter
    }
}