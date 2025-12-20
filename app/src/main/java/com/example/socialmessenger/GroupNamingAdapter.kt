package com.example.socialmessenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemContactBinding

class GroupNamingAdapter(
    private val items: List<ContactList>):
    RecyclerView.Adapter<GroupNamingAdapter.GroupNamingHolder>(){

    class GroupNamingHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactName: String){
            binding.tvContactName.text = contactName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupNamingHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return GroupNamingHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupNamingHolder, position: Int) {
        val conName = items[position].username
        holder.bind(conName)
    }

    override fun getItemCount(): Int = items.size
}
