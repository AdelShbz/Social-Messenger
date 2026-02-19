package com.example.socialmessenger

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemChatListBinding
import com.google.gson.Gson

class ChatListAdapter(
    private val items: List<ChatList>,
    private val activity: AppCompatActivity,
    private val self_username: String,
    private val groupList: List<RoomGroup>):
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>(){
    var membersInContactList = mutableListOf<ContactList>()
    class ChatListViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nameItem:String, textItem: String, openRoom:()-> Unit){
            binding.tvChatName.text = nameItem
            binding.tvLastMessage.text = textItem
            binding.root.setOnClickListener { openRoom() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder,position: Int) {
        val chatName = items[position].chatName
        val text = items[position].lastMessage
//        val type = groupList[position].type
        val type = "private"
//        val type = if (position < groupList.size) groupList[position].type else "private"
        holder.bind(chatName, text, fun (){
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("SELF_USERNAME", self_username)
            intent.putExtra("TYPE", type)
            if (type == "private"){
                intent.putExtra("OTHER_USERNAME", chatName)
            } else {
                intent.putExtra("ID", groupList[position]._id)
                intent.putExtra("ROOM_NAME", groupList[position].roomName)
                groupList[position].members.forEach { member ->
                    val cl = ContactList(member,"")
                    membersInContactList.add(cl)
                }
                val membersJson = Gson().toJson(membersInContactList)
                intent.putExtra("MEMBERS", membersJson)
            }
            activity.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = items.size


}