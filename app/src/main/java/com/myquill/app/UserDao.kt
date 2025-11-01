package com.myquill.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE (email = :id OR username = :id) AND password = :password LIMIT 1")
    suspend fun findByEmailOrUsernameAndPassword(id: String, password: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE email = :email OR username = :username")
    suspend fun existsByEmailOrUsername(email: String, username: String): Int

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteById(userId: Long)
}