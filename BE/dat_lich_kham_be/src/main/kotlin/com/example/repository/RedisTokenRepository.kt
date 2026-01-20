package com.example.repository

import com.example.config.RedisConfig
import io.lettuce.core.SetArgs
import java.time.Duration

/**
 * Repository quản lý Token Blacklist bằng Redis
 */
class RedisTokenRepository(private val redisConfig: RedisConfig) {

    private val commands = redisConfig.commands

    //  Prefix cho các key
    private val BLACKLIST_PREFIX = "blacklist:token:"
    private val USER_TOKENS_PREFIX = "user:tokens:"

    /**
     * Thêm token vào blacklist
     * @param tokenJti: JWT ID của token
     * @param ttlSeconds: Thời gian sống (giây) - thường là thời gian còn lại của token
     */
    fun addToBlacklist(tokenJti: String, ttlSeconds: Long): Boolean {
        return try {
            val key = "$BLACKLIST_PREFIX$tokenJti"

            //  Set key với TTL (Redis tự động xóa sau khi hết hạn)
            commands.setex(key, ttlSeconds, "revoked")

            println(" Token added to blacklist: $tokenJti (TTL: ${ttlSeconds}s)")
            true
        } catch (e: Exception) {
            println(" Error adding token to blacklist: ${e.message}")
            false
        }
    }

    /**
     * Kiểm tra token có trong blacklist không
     */
    fun isBlacklisted(tokenJti: String): Boolean {
        return try {
            val key = "$BLACKLIST_PREFIX$tokenJti"
            commands.exists(key) > 0
        } catch (e: Exception) {
            println(" Error checking blacklist: ${e.message}")
            false
        }
    }

    /**
     * Xóa token khỏi blacklist (thường không cần vì có TTL)
     */
    fun removeFromBlacklist(tokenJti: String): Boolean {
        return try {
            val key = "$BLACKLIST_PREFIX$tokenJti"
            commands.del(key) > 0
        } catch (e: Exception) {
            println(" Error removing from blacklist: ${e.message}")
            false
        }
    }

    /**
     * Lưu token hợp lệ của user (dùng cho whitelist - optional)
     * @param userId: ID user
     * @param tokenJti: JWT ID
     * @param ttlSeconds: Thời gian sống
     */
    fun saveUserToken(userId: Int, tokenJti: String, ttlSeconds: Long): Boolean {
        return try {
            val key = "$USER_TOKENS_PREFIX$userId"

            //  Lưu token vào Set với TTL
            commands.sadd(key, tokenJti)
            commands.expire(key, ttlSeconds)

            println(" User token saved: user=$userId, jti=$tokenJti")
            true
        } catch (e: Exception) {
            println(" Error saving user token: ${e.message}")
            false
        }
    }

    /**
     * Lấy tất cả token của user
     */
    fun getUserTokens(userId: Int): Set<String> {
        return try {
            val key = "$USER_TOKENS_PREFIX$userId"
            commands.smembers(key) ?: emptySet()
        } catch (e: Exception) {
            println(" Error getting user tokens: ${e.message}")
            emptySet()
        }
    }

    /**
     * Blacklist tất cả token của user (dùng khi đổi mật khẩu)
     * @param userId: ID user
     * @param ttlSeconds: Thời gian blacklist (thường = refresh token expiry)
     */
    fun blacklistAllUserTokens(userId: Int, ttlSeconds: Long): Int {
        return try {
            val tokens = getUserTokens(userId)
            var count = 0

            tokens.forEach { tokenJti ->
                if (addToBlacklist(tokenJti, ttlSeconds)) {
                    count++
                }
            }

            // Xóa danh sách token của user
            commands.del("$USER_TOKENS_PREFIX$userId")

            println(" Blacklisted $count tokens for user $userId")
            count
        } catch (e: Exception) {
            println(" Error blacklisting user tokens: ${e.message}")
            0
        }
    }

    /**
     * Lấy số lượng token trong blacklist (debug)
     */
    fun getBlacklistCount(): Long {
        return try {
            val keys = commands.keys("$BLACKLIST_PREFIX*")
            keys?.size?.toLong() ?: 0L
        } catch (e: Exception) {
            println(" Error counting blacklist: ${e.message}")
            0L
        }
    }

    /**
     * Xóa toàn bộ blacklist (NGUY HIỂM - chỉ dùng cho testing)
     */
    fun clearBlacklist(): Boolean {
        return try {
            val keys = commands.keys("$BLACKLIST_PREFIX*")
            if (!keys.isNullOrEmpty()) {
                commands.del(*keys.toTypedArray())
            }
            println(" Blacklist cleared")
            true
        } catch (e: Exception) {
            println(" Error clearing blacklist: ${e.message}")
            false
        }
    }

    /**
     * Lấy thời gian còn lại của token trong blacklist
     */
    fun getTokenTTL(tokenJti: String): Long {
        return try {
            val key = "$BLACKLIST_PREFIX$tokenJti"
            commands.ttl(key)
        } catch (e: Exception) {
            println(" Error getting TTL: ${e.message}")
            -1L
        }
    }
}