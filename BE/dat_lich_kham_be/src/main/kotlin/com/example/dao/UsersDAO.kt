package com.example.dao

import com.example.Tables.Users
import com.example.utils.toKotlinxLocalDate
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UsersDAO(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UsersDAO>(Users)

    var accountId by Users.accountId
    var fullName by Users.fullName
    var gender by Users.gender
    var birthDate by Users.birthDate
    var cccd by Users.cccd
    var hometown by Users.hometown
    var balance by Users.balance
    var pincode by Users.pincode
    var imageurl by Users.imageurl
    var phone by Users.phone

    fun toModel(): com.example.models.Users {
        return com.example.models.Users(
            id = id.value,
            accountId = accountId.value, // .value để lấy Int từ EntityID
            fullName = fullName,
            gender = gender,
            birthDate = birthDate.toKotlinxLocalDate(),
            cccd = cccd,
            hometown = hometown,
            balance = balance,
            pincode = pincode,
            imageurl = imageurl,
            phone = phone
        )
    }
}