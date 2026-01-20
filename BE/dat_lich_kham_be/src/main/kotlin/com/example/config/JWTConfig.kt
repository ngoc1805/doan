package com.example.config

data class JWTConfig(
    val domain: String,
    val audience: String,
    val realm: String,
    val secret: String,
    val accessTokenExpiry: Long = 1000L * 60 * 5,
    val refreshTokenExpiry: Long = 3600000 * 24 * 30
)
