package com.example.socialmessenger

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemChatListBinding

class ChatListAdapter(
    private val items: List<ChatList>,
    private val activity: AppCompatActivity,
    private val self_username: String):
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>(){

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
        holder.bind(chatName, text, fun (){
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra("SELF_USERNAME", self_username)
            intent.putExtra("OTHER_USERNAME", chatName)
            activity.startActivity(intent)
        })
    }

    override fun getItemCount(): Int = items.size


}