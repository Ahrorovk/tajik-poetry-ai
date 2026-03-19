package com.example.choronopoets.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.choronopoets.dataClass.ChatMessageEntity
import com.example.choronopoets.dataClass.ChatSessionSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatHistoryDao {

    @Query("SELECT * FROM chat_history WHERE sessionKey = :sessionKey ORDER BY timestamp ASC")
    fun getMessages(sessionKey: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_history WHERE sessionKey = :sessionKey")
    suspend fun clearHistory(sessionKey: String)

    @Query("""
        SELECT 
            h.sessionKey,
            (SELECT content FROM chat_history WHERE sessionKey = h.sessionKey ORDER BY timestamp DESC LIMIT 1) AS lastMessage,
            MAX(h.timestamp) AS lastTimestamp,
            COUNT(*) AS messageCount
        FROM chat_history h
        GROUP BY h.sessionKey
        ORDER BY lastTimestamp DESC
    """)
    fun getAllSessions(): Flow<List<ChatSessionSummary>>
}
