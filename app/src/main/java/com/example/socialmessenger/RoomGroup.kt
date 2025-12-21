package com.example.socialmessenger

data class RoomGroup (
    var _id: String,
    val type: String,
    val members: MutableList<String> = mutableListOf(),
    val chats: MutableList<Chat> = mutableListOf(),
    val roomName: String,
    val roles: GroupRoles
)