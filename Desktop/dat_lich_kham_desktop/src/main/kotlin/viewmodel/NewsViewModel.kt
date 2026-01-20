package viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.News
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.http.HttpClient
import javax.xml.parsers.DocumentBuilderFactory
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.launch
import org.w3c.dom.Element


class NewsViewModel : ScreenModel {
    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val client = HttpClient(CIO)

    fun fetchNews() {
        screenModelScope.launch(Dispatchers.Default) {
            _isRefreshing.value = true
            try {
                val xml = client.get("https://vnexpress.net/rss/suc-khoe.rss").bodyAsText()
                _newsList.value = parseRss(xml)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _isRefreshing.value = false
        }
    }

    private fun parseRss(xmlString: String): List<News> {
        val list = mutableListOf<News>()
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(InputSource(StringReader(xmlString)))
        val items = doc.getElementsByTagName("item")
        for (i in 0 until items.length) {
            val item = items.item(i) as Element
            val title = item.getElementsByTagName("title").item(0)?.textContent ?: ""
            val link = item.getElementsByTagName("link").item(0)?.textContent ?: ""
            val description = item.getElementsByTagName("description").item(0)?.textContent ?: ""
            val enclosure = item.getElementsByTagName("enclosure")
            val imageUrl = if (enclosure.length > 0) {
                (enclosure.item(0) as? Element)?.getAttribute("url") ?: ""
            } else ""
            list.add(News(title, description, imageUrl, link))
        }
        return list
    }
}