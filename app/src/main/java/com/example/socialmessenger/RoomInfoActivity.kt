package com.example.socialmessenger

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmessenger.databinding.ActivityRoomInfoBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomInfoActivity : AppCompatActivity() {
    lateinit var binding: ActivityRoomInfoBinding
    private lateinit var adapter: MemberListAdapter
    private val memberList = mutableListOf<Member>()
    private var gson: Gson = Gson()
    lateinit var ourRoom: Room
    lateinit var self_username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRoomInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val ourRoomInString = intent.getStringExtra("ROOM")
        val typeForConvert = object : TypeToken<Room> () {}.type
        ourRoom = gson.fromJson(ourRoomInString, typeForConvert)
        self_username = intent.getStringExtra("SELF_USERNAME").toString()
        binding.tvRoomNameInfo.text = ourRoom.roomName
        binding.tvRoomTypeInfo.text = ourRoom.type
        var tempMember: Member
        ourRoom.members.forEach { member ->
            tempMember = Member(member,"")
            ourRoom.roles.admins.forEach { admin ->
                if (admin == member){
                    tempMember.role = "admin"
                }
            }
            if (ourRoom.roles.owner == member){
                tempMember.role = "owner"
            }
            memberList.add(tempMember)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        adapter = MemberListAdapter(memberList)
        val layoutmanager = LinearLayoutManager(this)
        binding.rvMembers.layoutManager = layoutmanager
        binding.rvMembers.adapter = adapter
    }
}