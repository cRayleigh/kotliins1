package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

data class User(val name: String, val age: Int)

object Users : Table()
{
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 255)
    val age: Column<Int> = integer("age")

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID")
}

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
    Database.connect("jdbc:postgresql://0.0.0.0:5432/test", driver = "org.postgresql.Driver",
        user = "admin", password = "admin")
    transaction {
        SchemaUtils.create(Users)
    }
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureSecurity()
    configureRouting()
}
