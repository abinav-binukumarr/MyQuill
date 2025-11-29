package com.myquill.app

import androidx.room.Entity

@Entity(
    tableName = "friend_requests",
    primaryKeys = ["fromUserId", "toUserId"]
)
data class FriendRequest(
    val fromUserId: Long,
    val toUserId: Long,
    val status: String
)
