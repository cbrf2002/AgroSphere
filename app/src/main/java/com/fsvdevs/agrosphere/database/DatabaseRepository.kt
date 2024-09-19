package com.fsvdevs.agrosphere.database

import java.sql.ResultSet
import java.sql.Statement


class DatabaseRepository {
    fun fetchDataFromDatabase(query: String): ResultSet? {
        val databaseConnection = DatabaseConnection()
        val connection = databaseConnection.getConnection()

        connection?.let {
            try {
                val statement: Statement = it.createStatement()
                return statement.executeQuery(query)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                connection.close()
            }
        }
        return null
    }
}