package com.example.dat_lich_kham_fe.cache

import com.example.dat_lich_kham_fe.data.model.DoctorResponse

object DoctorCache {
    // Key là Pair<departmentId, page>
    val doctorsMap = mutableMapOf<Pair<Int, Int>, List<DoctorResponse>>()
    // Hoặc nếu chỉ cần list tổng hợp:
    var doctors: MutableList<DoctorResponse> = mutableListOf()
}
