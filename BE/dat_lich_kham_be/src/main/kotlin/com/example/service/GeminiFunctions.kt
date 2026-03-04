package com.example.service

/**
 * Định nghĩa các Function Declarations cho Gemini AI Function Calling
 * 
 * Các functions này sẽ được Gemini AI tự động gọi khi cần thiết để:
 * - Lấy danh sách khoa
 * - Lấy danh sách bác sĩ theo khoa
 * - Kiểm tra lịch trống
 * - Tạo appointment
 * - Kiểm tra số dư
 */
object GeminiFunctions {
    
    /**
     * Function: Lấy danh sách tất cả các khoa trong bệnh viện
     * Gemini sẽ gọi khi cần thông tin về các khoa để gợi ý cho user
     */
    const val GET_DEPARTMENTS = """
    {
      "name": "get_departments",
      "description": "Lấy danh sách tất cả các khoa trong bệnh viện. Sử dụng khi cần gợi ý khoa phù hợp với triệu chứng của bệnh nhân.",
      "parameters": {
        "type": "object",
        "properties": {},
        "required": []
      }
    }
    """
    
    /**
     * Function: Lấy danh sách bác sĩ theo khoa
     * Gemini sẽ gọi sau khi user chọn hoặc đồng ý với khoa được gợi ý
     */
    const val GET_DOCTORS_BY_DEPARTMENT = """
    {
      "name": "get_doctors_by_department",
      "description": "Lấy danh sách bác sĩ theo khoa cụ thể. Sử dụng khi user đã chọn khoa và muốn xem danh sách bác sĩ.",
      "parameters": {
        "type": "object",
        "properties": {
          "department_id": {
            "type": "integer",
            "description": "ID của khoa cần lấy danh sách bác sĩ"
          }
        },
        "required": ["department_id"]
      }
    }
    """
    
    /**
     * Function: Lấy các khung giờ còn trống của bác sĩ
     * Gemini sẽ gọi sau khi user chọn bác sĩ và ngày khám
     */
    const val GET_AVAILABLE_TIME_SLOTS = """
    {
      "name": "get_available_time_slots",
      "description": "Lấy các khung giờ còn trống của bác sĩ trong ngày cụ thể. Ngày phải ở định dạng yyyy-MM-dd. Bệnh viện KHÔNG làm việc vào Chủ nhật.",
      "parameters": {
        "type": "object",
        "properties": {
          "doctor_id": {
            "type": "integer",
            "description": "ID của bác sĩ"
          },
          "date": {
            "type": "string",
            "description": "Ngày khám theo định dạng yyyy-MM-dd (ví dụ: 2026-02-10). Chuyển đổi từ dd/MM/yyyy nếu cần."
          }
        },
        "required": ["doctor_id", "date"]
      }
    }
    """
    
    /**
     * Function: Tạo lịch hẹn khám bệnh
     * Gemini CHỈ gọi sau khi user đã xác nhận tất cả thông tin và đồng ý thanh toán cọc
     */
    const val CREATE_APPOINTMENT = """
    {
      "name": "create_appointment",
      "description": "Tạo lịch hẹn khám bệnh cho người dùng. CHỈ GỌI sau khi user đã xác nhận: bác sĩ, ngày, giờ, và đồng ý thanh toán cọc 100,000 VND. Sẽ tự động trừ tiền cọc từ tài khoản user.",
      "parameters": {
        "type": "object",
        "properties": {
          "user_id": {
            "type": "integer",
            "description": "ID của người dùng (sẽ được tự động inject)"
          },
          "doctor_id": {
            "type": "integer",
            "description": "ID của bác sĩ đã được user chọn"
          },
          "exam_date": {
            "type": "string",
            "description": "Ngày khám theo định dạng yyyy-MM-dd"
          },
          "exam_time": {
            "type": "string",
            "description": "Giờ khám theo định dạng HH:mm (ví dụ: 14:00)"
          }
        },
        "required": ["user_id", "doctor_id", "exam_date", "exam_time"]
      }
    }
    """
    
    /**
     * Function: Kiểm tra số dư tài khoản user
     * Gemini sẽ gọi TRƯỚC KHI tạo appointment để đảm bảo user có đủ tiền cọc
     */
    const val CHECK_USER_BALANCE = """
    {
      "name": "check_user_balance",
      "description": "Kiểm tra số dư tài khoản người dùng. Gọi TRƯỚC KHI tạo appointment để xác nhận user có đủ 100,000 VND để thanh toán cọc.",
      "parameters": {
        "type": "object",
        "properties": {
          "user_id": {
            "type": "integer",
            "description": "ID của người dùng (sẽ được tự động inject)"
          }
        },
        "required": ["user_id"]
      }
    }
    """
    
    /**
     * Danh sách tất cả functions (dạng JSON strings)
     * Sẽ được parse và gửi cho Gemini API
     */
    val ALL_FUNCTIONS = listOf(
        GET_DEPARTMENTS,
        GET_DOCTORS_BY_DEPARTMENT,
        GET_AVAILABLE_TIME_SLOTS,
        CREATE_APPOINTMENT,
        CHECK_USER_BALANCE
    )
}
