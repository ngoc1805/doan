//package com.example.dat_lich_kham_fe.util
//
//import android.content.Context
//import android.content.Intent
//import android.net.Uri
//import com.example.dat_lich_kham_fe.data.api.address
//import kotlinx.coroutines.*
//import okhttp3.*
//import okhttp3.MediaType.Companion.toMediaTypeOrNull
//import org.json.JSONObject
//
//// Giao dịch bằng app MoMo
//fun requestPayUrl(context: Context, amount: String, callback: (payUrl: String?, error: String?) -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val client = OkHttpClient()
//            val json = JSONObject()
//            json.put("amount", amount)
//            json.put("orderInfo", "Thanh toán MoMo app")
//            // Thêm amount vào redirectUrl để có thể lấy được khi quay lại
//            json.put("redirectUrl", "datlichkham://callback?amount=$amount")
//            json.put("ipnUrl", "$address/api/momo/ipn")
//            val body = RequestBody.create(
//                "application/json; charset=utf-8".toMediaTypeOrNull(),
//                json.toString()
//            )
//            val request = Request.Builder()
//                .url("$address/api/momo/pay")
//                .post(body)
//                .build()
//            val res = client.newCall(request).execute()
//            val resBody = res.body?.string()
//            val obj = JSONObject(resBody ?: "")
//            val payUrl = obj.optString("payUrl", null)
//            val resultCode = obj.optInt("resultCode", -1)
//            if (payUrl != null && resultCode == 0) {
//                withContext(Dispatchers.Main) { callback(payUrl, null) }
//            } else {
//                val msg = obj.optString("message", "Không lấy được payUrl")
//                withContext(Dispatchers.Main) { callback(null, msg) }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) { callback(null, e.message) }
//        }
//    }
//}
//
//// Tạo QR cho giao dịch MoMo
//fun requestQrUrl(context: Context, amount: String, callback: (qrUrl: String?, error: String?) -> Unit) {
//    CoroutineScope(Dispatchers.IO).launch {
//        try {
//            val client = OkHttpClient()
//            val json = JSONObject()
//            json.put("amount", amount)
//            json.put("orderInfo", "Thanh toán MoMo QR")
//            val body = RequestBody.create(
//                "application/json; charset=utf-8".toMediaTypeOrNull(),
//                json.toString()
//            )
//            val request = Request.Builder()
//                .url("$address/api/momo/qr")
//                .post(body)
//                .build()
//            val res = client.newCall(request).execute()
//            val resBody = res.body?.string()
//            val obj = JSONObject(resBody ?: "")
//            val qrCodeUrl = obj.optString("qrCodeUrl", null)
//            val resultCode = obj.optInt("resultCode", -1)
//            if (qrCodeUrl != null && resultCode == 0) {
//                withContext(Dispatchers.Main) { callback(qrCodeUrl, null) }
//            } else {
//                val msg = obj.optString("message", "Không lấy được qrCodeUrl")
//                withContext(Dispatchers.Main) { callback(null, msg) }
//            }
//        } catch (e: Exception) {
//            withContext(Dispatchers.Main) { callback(null, e.message) }
//        }
//    }
//}
//
//// Hàm mở app MoMo bằng url
//fun openMomoApp(context: Context, payUrl: String) {
//    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(payUrl))
//    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//    context.startActivity(intent)
//}

//--------------------------------------------------------------
package com.example.dat_lich_kham_fe.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.dat_lich_kham_fe.data.api.address
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject

// Giao dịch bằng app MoMo
fun requestPayUrl(context: Context, amount: String, callback: (payUrl: String?, error: String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val json = JSONObject()
            json.put("amount", amount)
            json.put("orderInfo", "Nạp tiền MoMo app")
            json.put("redirectUrl", "datlichkham://callback?amount=$amount")
            json.put("ipnUrl", "$address/api/momo/ipn")

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val request = Request.Builder()
                .url("$address/api/momo/pay")
                .post(body)
                .build()
            val res = client.newCall(request).execute()
            val resBody = res.body?.string()
            val obj = JSONObject(resBody ?: "")
            val payUrl = obj.optString("payUrl", null)
            val resultCode = obj.optInt("resultCode", -1)
            if (payUrl != null && resultCode == 0) {
                withContext(Dispatchers.Main) { callback(payUrl, null) }
            } else {
                val msg = obj.optString("message", "Không lấy được payUrl")
                withContext(Dispatchers.Main) { callback(null, msg) }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { callback(null, e.message) }
        }
    }
}

// Tạo QR code MoMo với description (userId)
fun requestQrUrl(
    context: Context,
    amount: String,
    description: String,
    paymentMethod: String = "card",
    callback: (qrCodeUrl: String?, orderId: String?, error: String?) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val json = JSONObject()
            json.put("amount", amount)
            json.put("orderInfo", "Nạp tiền vào tài khoản")
            json.put("description", description)
            json.put("paymentMethod", paymentMethod)

            val body = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                json.toString()
            )
            val request = Request.Builder()
                .url("$address/api/momo/qr")
                .post(body)
                .build()
            val res = client.newCall(request).execute()
            val resBody = res.body?.string()
            val obj = JSONObject(resBody ?: "")

            val qrCodeUrl = obj.optString("qrCodeUrl", null)
            val orderId = obj.optString("orderId", null)
            val resultCode = obj.optInt("resultCode", -1)

            if (qrCodeUrl != null && resultCode == 0) {
                withContext(Dispatchers.Main) { callback(qrCodeUrl, orderId, null) }
            } else {
                val msg = obj.optString("message", "Không tạo được QR")
                withContext(Dispatchers.Main) { callback(null, null, msg) }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { callback(null, null, e.message) }
        }
    }
}

// Kiểm tra trạng thái thanh toán
fun checkPaymentStatus(
    context: Context,
    orderId: String,
    callback: (status: String?, transId: String?, error: String?) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("$address/api/momo/status/$orderId")
                .get()
                .build()

            val res = client.newCall(request).execute()
            val resBody = res.body?.string()
            val obj = JSONObject(resBody ?: "")

            val status = obj.optString("status", null)
            val transId = obj.optString("transId", null)
            val message = obj.optString("message", null)

            withContext(Dispatchers.Main) {
                callback(status, transId, if (status == "error") message else null)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) { callback(null, null, e.message) }
        }
    }
}

// Hàm mở app MoMo bằng url
fun openMomoApp(context: Context, payUrl: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(payUrl))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}