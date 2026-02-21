package com.example.socialmessenger

data class Room(
    var _id: String,
    val type: String,
    val members: MutableList<String> = mutableListOf(),
    val chats: MutableList<Chat> = mutableListOf(),
    var roomName: String,
    val roles: GroupRoles
)