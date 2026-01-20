package com.example.dat_lich_kham_fe.ui.screen

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun Test() {
    Text(text = "Hello World!")
}

@Preview(showBackground = true)
@Composable
fun TestPreview() {
    MaterialTheme{
        Test()
    }
}
