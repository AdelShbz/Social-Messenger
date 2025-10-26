package com.example.socialmessenger

data class Room(
    val type: String,
    val members: MutableList<String> = mutableListOf(),
    val chats: MutableList<Chat> = mutableListOf()
)