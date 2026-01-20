package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import data.model.DoctorListItem
import data.repository.DoctorRepository
import data.model.InFoDoctorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DoctorViewModel : ScreenModel {
    private val doctorRepository = DoctorRepository()

    private val _doctorInfoState = MutableStateFlow<InFoDoctorResponse?>(null)
    val doctorInfoState: StateFlow<InFoDoctorResponse?> = _doctorInfoState

    var doctors by mutableStateOf<List<DoctorListItem>>(emptyList())

    fun fetchDoctorInfo(accountId: Int) {
        screenModelScope.launch(Dispatchers.Default) {
            val response = doctorRepository.getDoctorByAccountId(accountId)
            if (response.isSuccessful) {
                _doctorInfoState.value = response.body()
                // DoctorRepository đã lưu vào file rồi
            } else {
                _doctorInfoState.value = null
            }
        }
    }

    fun fetchDoctor(){
        screenModelScope.launch(Dispatchers.Default) {
            try {
                val response = doctorRepository.getAllDoctors()
                if(response.isSuccessful){
                    doctors = response.body()?.doctors ?: emptyList()
                }
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    // Hàm lấy nhanh từ file nếu cần
    fun getDoctorInfoFromFile(): InFoDoctorResponse? {
        return doctorRepository.getDoctorInfoFromFile()
    }
}