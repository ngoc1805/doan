package com.example.dat_lich_kham_fe.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dat_lich_kham_fe.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val context: Context) : ViewModel() {
    var phoneNumber by mutableStateOf("")
        private set
    var passWord by mutableStateOf("")
        private set
    var confirmPassWord by mutableStateOf("")
        private set
    var isSignUpSuccessful by mutableStateOf(false)
        private set

    var numberPhoneErr by mutableStateOf<String?>(null)
        private set
    var passWordErr by mutableStateOf<String?>(null)
        private set
    var confirmPassWordErr by mutableStateOf<String?>(null)
        private set

    var registerError by mutableStateOf<String?>(null)
        private set

    fun onPhoneNumberChanged(newPhoneNumber: String){
        phoneNumber = newPhoneNumber
        numberPhoneErr = null

    }
    fun onPassWordChanged(newPassWord: String){
        passWord = newPassWord
        passWordErr = null
    }

    fun onConfirmPassWordChanged(newConfirmPassWork: String){
        confirmPassWord = newConfirmPassWork
        confirmPassWordErr = null
    }
    private val repository = AuthRepository(context)

    fun validateAndRegister(onSuccess: () -> Unit, onFail: (String) -> Unit){
        val isPhoneNotEmpty = phoneNumber.isNotEmpty()
        val isPhoneVailiLength = phoneNumber.length == 10
        val isPhoneNumberIc = phoneNumber.all { it.isDigit() }

        val isPassWordNotEmpty = passWord.isNotEmpty()
        val isPassWordValidiLength = passWord.length >=6

        val ispassWordAndConfirmPassWordMatch = passWord == confirmPassWord
        val isConfirmPassWorkNotEmpty = confirmPassWord.isNotEmpty()

        numberPhoneErr = when
        {
            !isPhoneNotEmpty -> "Số điện thoại không được bỏ trống"
            !isPhoneVailiLength -> "Số điện thoại phải đúng 10 ký tự"
            !isPhoneNumberIc -> "Số điện thoại chỉ bao gồm các số"
            else -> null
        }

        passWordErr = when
        {
            !isPassWordNotEmpty -> "Mật khẩu không được bỏ trống"
            !isPassWordValidiLength -> "Mật khẩu tối thiểu 6 ký tự"
            else -> null
        }

        confirmPassWordErr = when
        {
            !isConfirmPassWorkNotEmpty -> "Xác nhận mật khẩu không được bỏ trống"
            !ispassWordAndConfirmPassWordMatch -> "Xác nhận mật khẩu không khớp"
            else -> null
        }
        if(numberPhoneErr == null && passWordErr == null && confirmPassWordErr == null){
            viewModelScope.launch {
                try {
                    val response = repository.register(phoneNumber, passWord, 1)
                    if(response.isSuccessful) {
                        isSignUpSuccessful = true
                        registerError = null
                        onSuccess()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        val errorMessage = if (!errorBody.isNullOrEmpty()) {
                            try {
                                org.json.JSONObject(errorBody).optString("message", response.message())
                            } catch (e: Exception) {
                                response.message()
                            }
                        } else {
                            response.message()
                        }
                        isSignUpSuccessful = false
                        registerError = errorMessage
                        onFail(errorMessage)
                    }

                }catch (e: Exception){
                    isSignUpSuccessful = false
                    registerError = e.message
                    onFail(e.message ?: e.toString())
                }
            }
        }
    }
    fun reset() {
        phoneNumber = ""
        passWord = ""
        confirmPassWord = ""
        isSignUpSuccessful = false
        numberPhoneErr = null
        passWordErr = null
        confirmPassWordErr = null
    }

}
