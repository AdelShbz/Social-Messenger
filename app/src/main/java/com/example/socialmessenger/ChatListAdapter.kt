package com.example.socialmessenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemChatListBinding

class ChatListAdapter(
    private val items: List<ChatList>):
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>(){

    class ChatListViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(nameItem:String, textItem: String){
            binding.tvChatName.text = nameItem
            binding.tvLastMessage.text = textItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ItemChatListBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder,position: Int) {
        val chatName = items[position].chatName
        val text = items[position].lastMessage
        holder.bind(chatName, text)
    }

    override fun getItemCount(): Int = items.size


}