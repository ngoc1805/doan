package com.example.dat_lich_kham_fe.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

var userId by mutableStateOf(0)
var fullName by mutableStateOf("")
var fcmToken by mutableStateOf("")
var balance by mutableStateOf(0)
var imageUrl by mutableStateOf("")
var birthDate by mutableStateOf("")
var gender by mutableStateOf("")
var cccd by mutableStateOf("")
