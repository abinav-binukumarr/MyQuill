package com.myquill.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface EntryDao {
    @Insert
    suspend fun insert(entry: Entry): Long

    @Update
    suspend fun update(entry: Entry)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM entries WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getForUser(userId: Long): List<Entry>

    @Query("SELECT * FROM entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Entry?

    @Query("DELETE FROM entries WHERE userId = :userId")
    suspend fun deleteByUser(userId: Long)
}