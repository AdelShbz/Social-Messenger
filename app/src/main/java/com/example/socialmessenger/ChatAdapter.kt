package com.example.socialmessenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemChatOtherBinding

class ChatAdapter(
    private val items: List<Chat> ):
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    class ChatViewHolder(private val binding: ItemChatOtherBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(username:String, text:String){
            binding.tvUsername.text = username
            binding.tvText.text = text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ChatViewHolder {
        val binding = ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder,position: Int) {
        val text = items[position].text
        val username = "username"
        holder.bind(username, text)
    }

    override fun getItemCount(): Int = items.size
}