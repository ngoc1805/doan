package card

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.smartcardio.*

/** AID của thẻ bệnh viện: 11 22 33 44 55 00 */
val HOSPITAL_AID: ByteArray = byteArrayOf(0x11, 0x22, 0x33, 0x44, 0x55, 0x00)

/**
 * Singleton giữ kết nối thẻ đang hoạt động.
 * Sống suốt vòng đời app — không bị mất khi navigate.
 */
object CardSession {

    private var _card: Card? = null
    private var _channel: CardChannel? = null
    private var _terminalName: String = ""
    private var _atr: String = ""

    val isConnected: Boolean get() = _channel != null && _card != null
    val terminalName: String get() = _terminalName
    val atr: String          get() = _atr
    val channel: CardChannel? get() = _channel

    /** Kết nối đầu đọc + SELECT AID bệnh viện */
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            disconnect()
            val factory   = TerminalFactory.getDefault()
            val terminals = factory.terminals().list()
            if (terminals.isEmpty()) error("Không tìm thấy đầu đọc thẻ nào")
            val terminal  = terminals.first()
            if (!terminal.isCardPresent) error("Không có thẻ trong đầu đọc\nHãy đặt thẻ vào trước khi kết nối")
            val card    = terminal.connect("*")
            val channel = card.basicChannel
            val selectAPDU = CommandAPDU(
                byteArrayOf(0x00, 0xA4.toByte(), 0x04, 0x00, HOSPITAL_AID.size.toByte()) + HOSPITAL_AID
            )
            val response = channel.transmit(selectAPDU)
            if (response.sw != 0x9000) {
                card.disconnect(false)
                error("Thẻ không hợp lệ — SW: ${response.sw.toString(16).uppercase()}\nVui lòng dùng thẻ bệnh viện đúng loại.")
            }
            _card = card; _channel = channel
            _terminalName = terminal.name
            _atr = card.atr.bytes.joinToString(" ") { "%02X".format(it) }
        }
    }

    /** Kiểm tra thẻ còn trong đầu đọc không */
    fun checkAlive(): Boolean = try {
        val terminals = TerminalFactory.getDefault().terminals().list()
        if (terminals.isEmpty()) { disconnect(); false }
        else { val ok = terminals.first().isCardPresent; if (!ok) disconnect(); ok }
    } catch (_: Exception) { disconnect(); false }

    fun disconnect() {
        try { _card?.disconnect(false) } catch (_: Exception) {}
        _card = null; _channel = null
    }

    /** Gửi APDU và trả về ResponseAPDU */
    fun transmit(apdu: ByteArray): Result<javax.smartcardio.ResponseAPDU> = runCatching {
        requireNotNull(_channel) { "Chưa kết nối thẻ" }
        _channel!!.transmit(CommandAPDU(apdu))
    }
}
