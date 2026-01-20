package com.example.repository

import com.example.Tables.Accounts
import com.example.Tables.Departments
import com.example.Tables.Roles
import com.example.dao.AccountDAO
import com.example.dao.RoleDAO
import com.example.config.DatabaseFactory.dbQuery
import com.example.dao.DoctorDAO
import com.example.dao.ServiceRoomDAO
import com.example.models.*
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt

class AccountRepository {

    suspend fun findByPhone(username: String): Account? = dbQuery {
        AccountDAO.find { Accounts.username eq username }
            .singleOrNull()
            ?.let { dao ->
                // Lấy role name bằng DAO nếu cần
                val roleName = dao.roleId?.let { roleId ->
                    RoleDAO.findById(roleId)?.roleName
                }
                Account(
                    id = dao.id.value,
                    username = dao.username,
                    roleId = dao.roleId?.value,
                    enabled = dao.enabled,
                    confirm = dao.confirm,
                    role = roleName
                )
            }
    }
    suspend fun findById(id: Int): Account? = dbQuery {
        AccountDAO.findById(id)
            ?.let { dao ->
                // Lấy role name bằng DAO nếu cần
                val roleName = dao.roleId?.let { roleId ->
                    RoleDAO.findById(roleId)?.roleName
                }
                Account(
                    id = dao.id.value,
                    username = dao.username,
                    roleId = dao.roleId?.value,
                    enabled = dao.enabled,
                    confirm = dao.confirm,
                    role = roleName
                )
            }
    }


    fun createAccount(username: String, password: String, roleId: Int?): Boolean = transaction {
        val exists = Accounts.select { Accounts.username eq username }.count() > 0
        if (exists) return@transaction false

        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        Accounts.insert {
            it[Accounts.username] = username
            it[Accounts.password] = hashedPassword
            it[Accounts.roleId] = roleId
            it[Accounts.enabled] = 1
            it[Accounts.confirm] = 0
        }
        true
    }

    suspend fun validatePassword(username: String, password: String): Boolean = dbQuery {
        AccountDAO.find { Accounts.username eq username }
            .singleOrNull()
            ?.let { BCrypt.checkpw(password, it.password) } ?: false
    }

    suspend fun getAccountWithRole(username: String): Account? = findByPhone(username)

    fun updateFmcToken(accountId: Int, fmcToken: String): Boolean = transaction {
        val account = AccountDAO.findById(accountId)
        if (account != null) {
            account.fmctoken = fmcToken
            true
        } else {
            false
        }
    }

    fun createDoctorWithAccount(
        name: String,
        code: String,
        departmentId: Int,
        examPrice: Int
    ): Boolean = transaction {
        // Check trùng username
        val exists = AccountDAO.find { com.example.Tables.Accounts.username eq code }.singleOrNull()
        if (exists != null) return@transaction false

        // Tạo tài khoản
        val hashedPassword = BCrypt.hashpw("123456", BCrypt.gensalt())
        val account = AccountDAO.new {
            this.username = code
            this.password = hashedPassword
            this.roleId = EntityID(2, Roles)
            this.enabled = 1
            this.confirm = 1
            this.fmctoken = ""
        }

        // Tạo bác sĩ
        DoctorDAO.new {
            this.name = name
            this.code = code
            this.accountId = account.id
            this.departmentId = EntityID(departmentId, Departments)
            this.examPrice = examPrice
            this.balance = 0
        }
        true
    }

    fun createServiceRoomWithAccount(
        name: String,
        code: String,
        address: String,
        examPrice: Int
    ): Boolean = transaction {
        // Kiểm tra trùng mã code (username)
        val exists = AccountDAO.find { Accounts.username eq code }.singleOrNull()
        if (exists != null) return@transaction false

        // Tạo tài khoản
        val hashedPassword = BCrypt.hashpw("123456", BCrypt.gensalt())
        val account = AccountDAO.new {
            this.username = code
            this.password = hashedPassword
            this.roleId = EntityID(3, Roles) // CHÚ Ý: phải là EntityID
            this.enabled = 1
            this.confirm = 1
            this.fmctoken = ""
        }

        // Tạo service room
        ServiceRoomDAO.new {
            this.name = name
            this.code = code
            this.accountId = account.id // đã là EntityID<Int>
            this.address = address
            this.examPrice = examPrice
        }
        true
    }

    //
    fun changePassword(accountId: Int, oldPassword: String?, newPassword: String): Boolean = transaction {
        val account = AccountDAO.findById(accountId)
        if (account != null) {
            // Nếu có oldPassword, kiểm tra đúng thì mới cho đổi
            if (oldPassword != null && !BCrypt.checkpw(oldPassword, account.password)) {
                return@transaction false
            }
            val hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())
            account.password = hashedPassword
            true
        } else {
            false
        }
    }

    fun saveOtpToAccount(username: String, otp: String): Boolean = transaction {
        val acc = AccountDAO.find { Accounts.username eq username }.singleOrNull()
        if (acc != null) {
            acc.twoFaKey = otp
            true
        } else false
    }

    fun verifyAndClearOtp(username: String, otp: String): Boolean = transaction {
        val acc = AccountDAO.find { Accounts.username eq username }.singleOrNull()
        if (acc != null && acc.twoFaKey == otp) {
            acc.twoFaKey = null
            true
        } else false
    }
    //

    // Thêm vào AccountRepository.kt
    fun resetPasswordWithToken(phone: String, newPassword: String): Boolean = transaction {
        val account = AccountDAO.find { Accounts.username eq phone }.singleOrNull()
        if (account != null) {
            val hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt())
            account.password = hashedPassword
            println(" Đã reset password cho $phone")
            true
        } else {
            println(" Không tìm thấy account với phone $phone")
            false
        }
    }

}