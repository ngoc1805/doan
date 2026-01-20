package com.example.dat_lich_kham_fe.ui.screen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavBackStackEntry

@Composable
fun DetailNewsScreen(
    navBackStackEntry: NavBackStackEntry
) {
    val link = navBackStackEntry.arguments?.getString("link") ?: ""

    AndroidView(factory = { context ->
        WebView(context).apply {
            webViewClient = WebViewClient()
            loadUrl(link)
        }
    })
}
