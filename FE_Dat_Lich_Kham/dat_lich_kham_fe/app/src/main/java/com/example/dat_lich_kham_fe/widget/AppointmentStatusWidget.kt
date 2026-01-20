package com.example.dat_lich_kham_fe.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import com.example.dat_lich_kham_fe.R
import com.example.dat_lich_kham_fe.data.api.RetrofitInstance
import com.example.dat_lich_kham_fe.data.model.AppointmentItem
import com.example.dat_lich_kham_fe.util.UserLocalStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class AppointmentStatusWidget : AppWidgetProvider() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_appointment_status)

        // Hiển thị trạng thái đang tải
        views.setTextViewText(R.id.tvWidgetLoading, "Đang tải...")
        appWidgetManager.updateAppWidget(appWidgetId, views)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val userStore = UserLocalStore(context)
                val user = userStore.getUser()
                val userId = user?.Id ?: 1

                val appointmentApi = RetrofitInstance.appointmentApi(context)
                val response: Response<AppointmentItem> = appointmentApi.nearestAppointment(userId)

                if (response.isSuccessful && response.body() != null) {
                    val appointment = response.body()!!
                    updateWidgetWithData(context, appWidgetManager, appWidgetId, views, appointment)
                } else {
                    views.setTextViewText(R.id.tvWidgetLoading, "Không có lịch khám")
                    appWidgetManager.updateAppWidget(appWidgetId, views)
                }
            } catch (e: Exception) {
                views.setTextViewText(R.id.tvWidgetLoading, "Lỗi kết nối")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }

        // Thêm PendingIntent để mở app khi click vào widget
        val intent = Intent(context, com.example.dat_lich_kham_fe.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widgetContainer, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    private fun updateWidgetWithData(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        views: RemoteViews,
        appointment: AppointmentItem
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Format ngày
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                val examDate = try {
                    val date = dateFormat.parse(appointment.examDate)
                    displayDateFormat.format(date ?: Date())
                } catch (e: Exception) {
                    appointment.examDate
                }

                // Format tiền
                val priceFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                val formattedPrice = "${priceFormat.format(appointment.examPrice)} VNĐ"

                // Tạo text hiển thị đầy đủ thông tin
                val appointmentInfo = buildString {
                    appendLine("📅 $examDate - ⏰ ${appointment.examTime}")
                    appendLine("👨‍⚕️ ${appointment.doctorName} (${appointment.doctorCode})")
                    appendLine("🏥 ${appointment.department}")
                    appendLine("💰 $formattedPrice")
                    append("📋 ${getStatusText(appointment.status)}")
                }

                views.setTextViewText(R.id.tvWidgetLoading, appointmentInfo)
                appWidgetManager.updateAppWidget(appWidgetId, views)

            } catch (e: Exception) {
                views.setTextViewText(R.id.tvWidgetLoading, "Lỗi hiển thị dữ liệu")
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }

    private fun getStatusText(status: String): String {
        return when (status.lowercase()) {
            "đã lên lịch" -> "Đã lên lịch"
            "đã khám" -> "Đã khám"
            "đã hủy" -> "Đã hủy"
            "chờ khám" -> "Chờ khám"
            else -> status
        }
    }
}
