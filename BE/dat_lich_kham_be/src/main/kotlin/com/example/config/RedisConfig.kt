package com.example.config

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands

/**
 * Redis Configuration
 */
class RedisConfig(
    host: String = "localhost",
    port: Int = 6379,
    password: String? = null,
    database: Int = 0
) {
    private val client: RedisClient
    val connection: StatefulRedisConnection<String, String>
    val commands: RedisCommands<String, String>

    init {
        //  Tạo Redis URI
        val redisUri = RedisURI.Builder
            .redis(host, port)
            .apply {
                if (!password.isNullOrBlank()) {
                    withPassword(password.toCharArray())
                }
                withDatabase(database)
            }
            .build()

        //  Kết nối Redis
        client = RedisClient.create(redisUri)
        connection = client.connect()
        commands = connection.sync()

        println(" Redis connected: $host:$port (database: $database)")
    }

    /**
     * Đóng kết nối Redis
     */
    fun close() {
        connection.close()
        client.shutdown()
        println(" Redis connection closed")
    }

    /**
     * Ping để kiểm tra kết nối
     */
    fun ping(): String {
        return commands.ping()
    }
}