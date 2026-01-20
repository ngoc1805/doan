package com.example.repository

import com.example.Tables.Accounts
import com.example.Tables.Users
import com.example.dao.UsersDAO
import com.example.dto.Request.UserRequest
import com.example.dto.Response.UserResponse
import com.example.utils.EncryptionUtil
import com.example.utils.toKotlinxLocalDate
import kotlinx.datetime.toJavaLocalDate
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt


class UserRepository {

//    fun updateOrCreateUserInfo(request: UserRequest): Boolean = transaction {
//        val userDao = UsersDAO.find { Users.accountId eq request.accountId }
//            .singleOrNull()
//        if (userDao != null) {
//            userDao.fullName = request.fullName
//            userDao.gender = request.gender
//            userDao.birthDate = request.birthDate.toJavaLocalDate()
//            userDao.cccd = request.cccd
//            userDao.hometown = request.hometown
//            true // Đã update
//        } else {
//            UsersDAO.new {
//                accountId = EntityID(request.accountId, Accounts)
//                fullName = request.fullName
//                gender = request.gender
//                birthDate = request.birthDate.toJavaLocalDate()
//                cccd = request.cccd
//                hometown = request.hometown
//                balance = 0 // hoặc giá trị mặc định
//            }
//            true // Đã tạo mới
//        }
//    }
fun updateOrCreateUserInfo(request: UserRequest): Boolean = transaction {
    val userDao = UsersDAO.find { Users.accountId eq request.accountId }
        .singleOrNull()
    if (userDao != null) {
        // Mã hóa các trường nhạy cảm trước khi update
        userDao.fullName = EncryptionUtil.encrypt(request.fullName).toString()
        userDao.gender = EncryptionUtil.encrypt(request.gender).toString()
        userDao.birthDate = request.birthDate.toJavaLocalDate()
        userDao.cccd = EncryptionUtil.encrypt(request.cccd).toString()
        userDao.hometown = EncryptionUtil.encrypt(request.hometown).toString()
        true // Đã update
    } else {
        UsersDAO.new {
            accountId = EntityID(request.accountId, Accounts)
            // Mã hóa các trường nhạy cảm khi tạo mới
            fullName = EncryptionUtil.encrypt(request.fullName).toString()
            gender = EncryptionUtil.encrypt(request.gender).toString()
            birthDate = request.birthDate.toJavaLocalDate()
            cccd = EncryptionUtil.encrypt(request.cccd).toString()
            hometown = EncryptionUtil.encrypt(request.hometown).toString()
            balance = 0
        }
        true // Đã tạo mới
    }
}

    //
//    fun isUserInfoComplete(accountId: Int): Boolean = transaction {
//        val userDao = UsersDAO.find { Users.accountId eq accountId }
//            .singleOrNull()
//        if (userDao == null) return@transaction false
//
//        !userDao.fullName.isNullOrBlank() &&
//                !userDao.gender.isNullOrBlank() &&
//                userDao.birthDate != null &&
//                !userDao.cccd.isNullOrBlank() &&
//                !userDao.hometown.isNullOrBlank()
//    }
    fun isUserInfoComplete(accountId: Int): Boolean = transaction {
        val userDao = UsersDAO.find { Users.accountId eq accountId }
            .singleOrNull()
        if (userDao == null) return@transaction false

        // Giải mã để kiểm tra
        val decryptedFullName = EncryptionUtil.decrypt(userDao.fullName)
        val decryptedGender = EncryptionUtil.decrypt(userDao.gender)
        val decryptedCccd = EncryptionUtil.decrypt(userDao.cccd)
        val decryptedHometown = EncryptionUtil.decrypt(userDao.hometown)

        !decryptedFullName.isNullOrBlank() &&
                !decryptedGender.isNullOrBlank() &&
                userDao.birthDate != null &&
                !decryptedCccd.isNullOrBlank() &&
                !decryptedHometown.isNullOrBlank()
    }

    //
//    fun getUserByAccountId(accountId: Int): UserResponse? = transaction {
//        val userDao = UsersDAO.find { Users.accountId eq accountId }
//            .singleOrNull()
//        userDao?.let {
//            UserResponse(
//                Id = it.id.value,
//                fullName = it.fullName,
//                gender = it.gender,
//                birthDate = it.birthDate.toKotlinxLocalDate(),
//                cccd = it.cccd,
//                hometown = it.hometown,
//                balance = it.balance,
//                imageurl = it.imageurl
//            )
//        }
//    }
    fun getUserByAccountId(accountId: Int): UserResponse? = transaction {
        val userDao = UsersDAO.find { Users.accountId eq accountId }
            .singleOrNull()
        userDao?.let {
            EncryptionUtil.decrypt(it.fullName)?.let { it1 ->
                EncryptionUtil.decrypt(it.gender)?.let { it2 ->
                    EncryptionUtil.decrypt(it.cccd)?.let { it3 ->
                        EncryptionUtil.decrypt(it.hometown)?.let { it4 ->
                            UserResponse(
                                Id = it.id.value,
                                // Giải mã các trường đã mã hóa
                                fullName = it1,
                                gender = it2,
                                birthDate = it.birthDate.toKotlinxLocalDate(),
                                cccd = it3,
                                hometown = it4,
                                balance = it.balance,
                                imageurl = it.imageurl
                            )
                        }
                    }
                }
            }
        }
    }

    //
    fun updateBalanceByUserId(userId: Int, balance: Int): Boolean = transaction {
        val userDao = UsersDAO.find { Users.id eq userId }.singleOrNull()
        if (userDao != null) {
            userDao.balance = balance
            true
        } else {
            false
        }
    }

    //
    fun updatePin(userId: Int, pinCode: String): Boolean = transaction {
        val userDao = UsersDAO.find { Users.id eq userId }.singleOrNull()
        if (userDao != null) {
            val hashedPin = BCrypt.hashpw(pinCode, BCrypt.gensalt())
            userDao.pincode = hashedPin
            true
        } else {
            false
        }
    }

    //
    fun comparePin(userId: Int, inputPin: String): Boolean = transaction {
        val userDao = UsersDAO.find { Users.id eq userId }.singleOrNull()
        val hashedPin = userDao?.pincode
        hashedPin != null && BCrypt.checkpw(inputPin, hashedPin)
    }


    //
    fun hasPin(userId: Int): Boolean = transaction {
        val userDao = UsersDAO.find { Users.id eq userId }.singleOrNull()
        val pin = userDao?.pincode
        !pin.isNullOrBlank()
    }
    //
//    fun getUserById(userId: Int): UserResponse? = transaction {
//        val userDao = UsersDAO.findById(userId)
//        userDao?.let {
//            UserResponse(
//                Id = it.id.value,
//                fullName = it.fullName,
//                gender = it.gender,
//                birthDate = it.birthDate.toKotlinxLocalDate(),
//                cccd = it.cccd,
//                hometown = it.hometown,
//                balance = it.balance,
//                imageurl = it.imageurl
//            )
//        }
//    }

    fun getUserById(userId: Int): UserResponse? = transaction {
        val userDao = UsersDAO.findById(userId)
        userDao?.let {
            EncryptionUtil.decrypt(it.gender)?.let { it1 ->
                EncryptionUtil.decrypt(it.fullName)?.let { it2 ->
                    EncryptionUtil.decrypt(it.cccd)?.let { it3 ->
                        EncryptionUtil.decrypt(it.hometown)?.let { it4 ->
                            UserResponse(
                                Id = it.id.value,
                                // Giải mã các trường đã mã hóa
                                fullName = it2,
                                gender = it1,
                                birthDate = it.birthDate.toKotlinxLocalDate(),
                                cccd = it3,
                                hometown = it4,
                                balance = it.balance,
                                imageurl = it.imageurl
                            )
                        }
                    }
                }
            }
        }
    }

    // Hoặc nếu chỉ cần balance, tạo hàm đơn giản hơn:
    fun getBalanceByUserId(userId: Int): Int? = transaction {
        val userDao = UsersDAO.findById(userId)
        userDao?.balance
    }
    //
    fun resetPinWithToken(accountId: Int, newPin: String): Boolean = transaction {
        val userDao = UsersDAO.find { Users.accountId eq accountId }.singleOrNull()
        if (userDao != null) {
            val hashedPin = BCrypt.hashpw(newPin, BCrypt.gensalt())
            userDao.pincode = hashedPin
            println(" Đã reset PIN cho accountId $accountId")
            true
        } else {
            println(" Không tìm thấy user với accountId $accountId")
            false
        }
    }

    /**
     * Đổi PIN (cần PIN cũ để xác thực)
     */
    fun changePinWithOldPin(userId: Int, oldPin: String, newPin: String): Boolean = transaction {
        val userDao = UsersDAO.find { Users.id eq userId }.singleOrNull()
        if (userDao != null) {
            val currentHashedPin = userDao.pincode

            // Kiểm tra PIN cũ
            if (currentHashedPin.isNullOrBlank() || !BCrypt.checkpw(oldPin, currentHashedPin)) {
                println(" PIN cũ không đúng cho userId $userId")
                return@transaction false
            }

            // Đổi PIN mới
            val hashedNewPin = BCrypt.hashpw(newPin, BCrypt.gensalt())
            userDao.pincode = hashedNewPin
            println(" Đã đổi PIN cho userId $userId")
            true
        } else {
            println(" Không tìm thấy user với userId $userId")
            false
        }
    }

}