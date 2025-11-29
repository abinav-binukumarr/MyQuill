package com.myquill.app

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["username"], unique = true)
    ]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val password: String,
    val profileImageUri: String? = null,
    val gender: String? = null,
    val age: Int? = null,
    val bio: String? = null
)
