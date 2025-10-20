package com.example.socialmessenger

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmessenger.databinding.ItemContactBinding

class ContactAdapter(
    private val items: List<ContactList>,
    private val activity: AppCompatActivity,
    private val token: String):
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(){
    class ContactViewHolder(private val binding: ItemContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactName: String, createRoom:()->Unit) {
            binding.tvContactName.text = contactName
            binding.root.setOnClickListener { createRoom() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder,position: Int) {
        val conName = items[position].username
        holder.bind(conName, fun (){
            val intentToMainActivity = Intent(activity, MainActivity::class.java)
            intentToMainActivity.putExtra("TOKEN",token)
            activity.startActivity(intentToMainActivity)
        })
    }

    override fun getItemCount(): Int = items.size


}