package com.example.socialmessenger

data class Chat (
    var text: String,
    val isSelf: Boolean = false
)