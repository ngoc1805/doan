package com.example.dat_lich_kham_fe.util

fun divideAndRound(input: Float): Float {
    return kotlin.math.round(input / 448.0f * 10) / 10.0f
}