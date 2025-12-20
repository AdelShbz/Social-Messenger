package com.example.socialmessenger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityGroupNamingBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GroupNamingActivity : AppCompatActivity() {
    lateinit var binding: ActivityGroupNamingBinding
    lateinit var token:String
    lateinit var self_username: String
    private var gson: Gson = Gson()
    private lateinit var adapter: GroupNamingAdapter
    var selectedItems = mutableListOf<ContactList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGroupNamingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        token = intent.getStringExtra("TOKEN").toString()
        self_username = intent.getStringExtra("USERNAME").toString()
        val selectedItemInString = intent.getStringExtra("SELECTED_ITEMS")
        val type = object : TypeToken<MutableList<ContactList>>() {}.type
        selectedItems = gson.fromJson(selectedItemInString, type)
        setupRecyclerView()
        adapter.notifyDataSetChanged()
        binding.buttonGroupNaming.setOnClickListener {
            if(binding.editTextGroupNaming.text.toString().isEmpty()){
                Toast.makeText(this, "نام گروه را وارد کنید!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val intent = Intent(this@GroupNamingActivity, MainActivity::class.java)
            intent.putExtra("TOKEN", token)
            intent.putExtra("USERNAME", self_username)
            val membersJson = gson.toJson(selectedItems)
            intent.putExtra("MEMBERS", membersJson)
            intent.putExtra("TYPE", "group")
            val groupName = binding.editTextGroupNaming.text.toString()
            intent.putExtra("ROOM_NAME",groupName)
            startActivity(intent)
        }
    }
    private fun setupRecyclerView(){
        adapter = GroupNamingAdapter(selectedItems)
        binding.rvGroupNaming.layoutManager = LinearLayoutManager(this)
        binding.rvGroupNaming.adapter = adapter
    }
}