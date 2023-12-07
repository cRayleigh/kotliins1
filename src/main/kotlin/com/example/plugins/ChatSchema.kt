package com.example.plugins

import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import java.sql.Connection
import java.sql.Statement

@Serializable
data class Chat(val name: String, val number_of_members: Int)
class ChatService(private val connection: Connection) {
    companion object {
        private const val CREATE_TABLE_CHATS =
            "CREATE TABLE CHATS (ID SERIAL PRIMARY KEY, NAME VARCHAR(255), number_of_members INT);"
        private const val SELECT_CHAT_BY_ID = "SELECT name, number_of_members FROM chats WHERE id = ?"
        private const val INSERT_CHAT = "INSERT INTO chats (name, number_of_members) VALUES (?, ?)"
        private const val UPDATE_CHAT = "UPDATE chats SET name = ?, number_of_members = ? WHERE id = ?"
        private const val DELETE_CHAT = "DELETE FROM chats WHERE id = ?"

    }

    init {
        val statement = connection.createStatement()
        statement.executeUpdate(CREATE_TABLE_CHATS)
    }

    private var newChatId = 0

    suspend fun create(chat: Chat): Int = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(INSERT_CHAT, Statement.RETURN_GENERATED_KEYS)
        statement.setString(1, chat.name)
        statement.setInt(2, chat.number_of_members)
        statement.executeUpdate()

        val generatedKeys = statement.generatedKeys
        if (generatedKeys.next()) {
            return@withContext generatedKeys.getInt(1)
        } else {
            throw Exception("Unable to retrieve the id of the newly inserted chat")
        }
    }

    suspend fun read(id: Int): Chat = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(SELECT_CHAT_BY_ID)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()

        if (resultSet.next()) {
            val name = resultSet.getString("name")
            val number_of_members = resultSet.getInt("number_of_members")
            return@withContext Chat(name, number_of_members)
        } else {
            throw Exception("Record not found")
        }
    }

    suspend fun update(id: Int, chat: Chat) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(UPDATE_CHAT)
        statement.setString(1, chat.name)
        statement.setInt(2, chat.number_of_members)
        statement.setInt(3, id)
        statement.executeUpdate()
    }

    suspend fun delete(id: Int) = withContext(Dispatchers.IO) {
        val statement = connection.prepareStatement(DELETE_CHAT)
        statement.setInt(1, id)
        statement.executeUpdate()
    }
}
