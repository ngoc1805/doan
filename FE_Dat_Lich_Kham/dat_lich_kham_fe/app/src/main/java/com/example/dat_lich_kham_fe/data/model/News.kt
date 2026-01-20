package com.example.dat_lich_kham_fe.model

data class News(
    val title: String,
    val description: String,
    val imageUrl: String = "",
    val link: String,
)
