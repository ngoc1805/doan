package card

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.smartcardio.*

/**
 * AID của thẻ bệnh viện: 11 22 33 44 55 00
 */
val HOSPITAL_AID: ByteArray = byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x00)

sealed class CardState {
    object Idle : CardState()
    object Scanning : CardState()
    data class Connected(val terminalName: String, val atr: String) : CardState()
    data class Error(val message: String) : CardState()
}

sealed class CardEvent {
    data class Connected(val terminalName: String, val atr: String) : CardEvent()
    object Disconnected : CardEvent()
    data class Error(val message: String) : CardEvent()
}

object CardManager {

    /**
     * Lấy danh sách đầu đọc thẻ có sẵn
     */
    suspend fun listTerminals(): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val factory = TerminalFactory.getDefault()
            factory.terminals().list().map { it.name }
        }
    }

    /**
     * Kết nối đến thẻ và SELECT AID của bệnh viện
     * @param onEvent callback khi có sự kiện thẻ
     */
    suspend fun connect(terminalName: String? = null): Result<CardChannel> = withContext(Dispatchers.IO) {
        runCatching {
            val factory   = TerminalFactory.getDefault()
            val terminals = factory.terminals().list()
            if (terminals.isEmpty()) error("Không tìm thấy đầu đọc thẻ nào")
            val terminal  = if (terminalName != null)
                terminals.firstOrNull { it.name == terminalName } ?: terminals.first()
            else terminals.first()

            if (!terminal.isCardPresent) error("Không có thẻ trong đầu đọc")

            val card    = terminal.connect("*")
            val channel = card.basicChannel

            // SELECT AID
            val selectAPDU = CommandAPDU(
                byteArrayOf(
                    0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
                    HOSPITAL_AID.size.toByte()
                ) + HOSPITAL_AID
            )
            val response = channel.transmit(selectAPDU)
            if (response.sw != 0x9000) {
                card.disconnect(false)
                error("Thẻ không hợp lệ (SW: ${response.sw.toString(16).uppercase()})")
            }
            channel
        }
    }

    /**
     * Theo dõi thẻ liên tục, gọi callback khi có thay đổi trạng thái
     */
    suspend fun monitorCard(
        pollIntervalMs: Long = 800,
        onEvent: (CardEvent) -> Unit
    ) = withContext(Dispatchers.IO) {
        var wasPresent = false
        while (true) {
            try {
                val factory   = TerminalFactory.getDefault()
                val terminals = factory.terminals().list()
                if (terminals.isNotEmpty()) {
                    val terminal = terminals.first()
                    val isPresent = terminal.isCardPresent
                    if (isPresent && !wasPresent) {
                        // Thẻ vừa được đặt vào
                        runCatching {
                            val card    = terminal.connect("*")
                            val channel = card.basicChannel
                            val atr     = card.atr.bytes.toHex()
                            // SELECT AID
                            val selectAPDU = CommandAPDU(
                                byteArrayOf(
                                    0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
                                    HOSPITAL_AID.size.toByte()
                                ) + HOSPITAL_AID
                            )
                            val response = channel.transmit(selectAPDU)
                            if (response.sw == 0x9000) {
                                onEvent(CardEvent.Connected(terminal.name, atr))
                            } else {
                                card.disconnect(false)
                                onEvent(CardEvent.Error("Thẻ không phải thẻ bệnh viện (SW: ${response.sw.toString(16).uppercase()})"))
                            }
                        }.onFailure { e ->
                            onEvent(CardEvent.Error(e.message ?: "Lỗi kết nối thẻ"))
                        }
                        wasPresent = true
                    } else if (!isPresent && wasPresent) {
                        // Thẻ vừa được rút ra
                        onEvent(CardEvent.Disconnected)
                        wasPresent = false
                    }
                }
            } catch (_: Exception) { }
            delay(pollIntervalMs)
        }
    }

    private fun ByteArray.toHex(): String =
        joinToString(" ") { "%02X".format(it) }
}
