package com.fsvdevs.agrosphere.database

import android.annotation.SuppressLint
import java.sql.Connection
import java.sql.DriverManager

@SuppressLint("AuthLeak")
private const val JDBC_URL = "jdbc:sqlserver://agrosphere-db-1.database.windows.net:1433;database=AgroSphereDB;user=fabiancr@agrosphere-db-1;password=zgnDqK7FC6ep;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"

class DatabaseConnection {
    var ip = "192.168.254.103"

    fun getConnection(): Connection? {
        return try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver")
            val connectionUrl = JDBC_URL
            DriverManager.getConnection(connectionUrl)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}

