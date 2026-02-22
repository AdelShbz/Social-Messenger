package com.example.socialmessenger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemMemberListBinding

class MemberListAdapter(
    private val items: List<Member>):
    RecyclerView.Adapter<MemberListAdapter.MemberListHolder>(){

    class MemberListHolder(private val binding: ItemMemberListBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(memberName: String, role: String){
            binding.tvMemberName.text = memberName
            binding.tvRole.text = role
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberListHolder {
        val binding = ItemMemberListBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MemberListHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberListHolder, position: Int) {
        val memberName = items[position].membername
        val memberRole = items[position].role
        holder.bind(memberName, memberRole)
    }

    override fun getItemCount(): Int = items.size



}