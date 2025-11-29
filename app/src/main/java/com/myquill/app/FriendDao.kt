package com.myquill.app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FriendDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(request: FriendRequest)

    @Query("UPDATE friend_requests SET status = :status WHERE fromUserId = :from AND toUserId = :to")
    suspend fun updateStatus(from: Long, to: Long, status: String)

    @Query("SELECT * FROM friend_requests WHERE toUserId = :userId AND status = 'pending'")
    suspend fun incomingRequests(userId: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_requests WHERE fromUserId = :userId AND status = 'pending'")
    suspend fun outgoingRequests(userId: Long): List<FriendRequest>

    @Query("SELECT * FROM friend_requests WHERE (fromUserId = :userId AND toUserId = :otherId) OR (fromUserId = :otherId AND toUserId = :userId) LIMIT 1")
    suspend fun relationBetween(userId: Long, otherId: Long): FriendRequest?

    @Query("DELETE FROM friend_requests WHERE (fromUserId = :userId AND toUserId = :otherId) OR (fromUserId = :otherId AND toUserId = :userId)")
    suspend fun removeRelation(userId: Long, otherId: Long)

    @Query("SELECT u.* FROM users u INNER JOIN friend_requests f ON ((f.fromUserId = :userId AND f.toUserId = u.id) OR (f.toUserId = :userId AND f.fromUserId = u.id)) WHERE f.status = 'accepted'")
    suspend fun friends(userId: Long): List<User>
}
