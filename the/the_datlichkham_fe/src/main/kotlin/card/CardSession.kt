package card

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.smartcardio.*

/**
 * AID của thẻ bệnh viện: 11 22 33 44 55 00
 */


/**
 * Singleton giữ kết nối thẻ đang hoạt động.
 * Sống suốt vòng đời app — không bị mất khi navigate.
 */
object CardSession {

    private var _card: Card? = null
    private var _channel: CardChannel? = null
    private var _terminalName: String = ""
    private var _atr: String = ""

    val isConnected: Boolean
        get() = _channel != null && _card != null

    val terminalName: String get() = _terminalName
    val atr: String get() = _atr
    val channel: CardChannel? get() = _channel

    /**
     * Kết nối đến đầu đọc thẻ + SELECT AID bệnh viện (11 22 33 44 55 00).
     */
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            disconnect()

            val factory   = TerminalFactory.getDefault()
            val terminals = factory.terminals().list()
            if (terminals.isEmpty()) error("Không tìm thấy đầu đọc thẻ nào")

            val terminal = terminals.first()
            if (!terminal.isCardPresent) error("Không có thẻ trong đầu đọc\nHãy đặt thẻ vào trước khi kết nối")

            val card    = terminal.connect("*")
            val channel = card.basicChannel

            // SELECT AID
            val selectAPDU = CommandAPDU(
                byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, HOSPITAL_AID.size.toByte())
                        + HOSPITAL_AID
            )
            val response = channel.transmit(selectAPDU)
            if (response.sw != 0x9000) {
                card.disconnect(false)
                error("Thẻ không hợp lệ — SW: ${response.sw.toString(16).uppercase()}\nVui lòng dùng thẻ bệnh viện đúng loại.")
            }

            _card         = card
            _channel      = channel
            _terminalName = terminal.name
            _atr          = card.atr.bytes.toHex()
        }
    }

    /**
     * Kiểm tra thẻ có còn trong đầu đọc không.
     * Nếu mất → tự disconnect và trả về false.
     */
    fun checkAlive(): Boolean = try {
        val terminals = TerminalFactory.getDefault().terminals().list()
        if (terminals.isEmpty()) { disconnect(); false }
        else {
            val present = terminals.first().isCardPresent
            if (!present) disconnect()
            present
        }
    } catch (_: Exception) { disconnect(); false }

    fun disconnect() {
        try { _card?.disconnect(false) } catch (_: Exception) {}
        _card    = null
        _channel = null
    }

    private fun ByteArray.toHex() = joinToString(" ") { "%02X".format(it) }
}
