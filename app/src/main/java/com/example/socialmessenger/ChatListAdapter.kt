package com.example.socialmessenger

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemChatListBinding
import com.google.gson.Gson

class ChatListAdapter(
    private val items: List<Room>,
    private val activity: AppCompatActivity,
    private val self_username: String):
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
        val type = items[position].type
        val members = items[position].members
        if (type == "private"){
            if (members[0] == self_username){
                items[position].roomName = members[1]
            } else {
                items[position].roomName = members[0]
            }
        }
        val chatName = items[position].roomName
        var text:String
        if (items[position].chats.size == 0){
            text = "پیامی وجود ندارد"
        } else {
            text = items[position].chats[items[position].chats.size - 1].text
        }
        holder.bind(chatName, text, fun (){
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("SELF_USERNAME", self_username)
            intent.putExtra("TYPE", type)
            intent.putExtra("ID", items[position]._id)
            if (type == "private"){
                intent.putExtra("OTHER_USERNAME", chatName)
            }
            else {
                intent.putExtra("ROOM_NAME", items[position].roomName)
                items[position].members.forEach { member ->
                    val cl = ContactList(member,"")
                    membersInContactList.add(cl)
                }
                val membersJson = Gson().toJson(membersInContactList)
//                val membersJson = Gson().toJson(items[position].members)
                intent.putExtra("MEMBERS", membersJson)
            }
            activity.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = items.size


}