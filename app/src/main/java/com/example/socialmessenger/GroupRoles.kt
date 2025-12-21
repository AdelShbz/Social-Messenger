package com.example.socialmessenger

data class GroupRoles (
    val owner: String,
    val admins: MutableList<String> = mutableListOf()
)