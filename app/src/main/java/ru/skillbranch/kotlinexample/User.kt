package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.*
import kotlin.reflect.KProperty

class User private constructor(
    private val firstName:String,
    private val lastName:String?,
    email:String? = null,
    rawPhone:String? = null,
    meta:Map<String, Any>? = null
) {
    val userInfo: String
    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase()}
            .joinToString(" ")

    private var phone: String? = null
        set(value) {
            field = value?.replace("[^+\\d]".toRegex(), "")
        }

    private var _login: String? = null
    internal var login: String
        set(value) {
            _login = value.toLowerCase(Locale.ROOT)
        }
        get() = _login!!

    private val _salt:String by lazy {
        ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()
    }

    private var salt: String? = null
        get() = (field ?: _salt)

    private lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    constructor(
        firstName: String,
        lastName: String?,
        email: String?,
        password: String
    ): this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ): this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
        println("Secondary phone constructor")
        requestAccessCode()
    }

    init {
        println("First init block, primary constructor was called")

        check(!firstName.isBlank()) { "FirstName must be not blank" }
        check(email.isNullOrBlank() || rawPhone.isNullOrBlank()) { "Email or phone must be not blank" }

        phone = rawPhone
        if (phone != null && phone?.length != 12 ) {
            throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
        }
        login = email  ?: phone!!

        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    fun checkPassword(pass:String) = encrypt(pass) == passwordHash

    fun changePassword(oldPass:String, newPass:String) {
        if (checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalArgumentException("Thr entered password dos not mach the current password")
    }

    fun requestAccessCode() {
        val code = generateAccessCode()
        passwordHash = encrypt(code)
        accessCode = code
        sendAccessCodeToUser(phone, code)
    }

    private fun encrypt(password: String):String  = salt.plus(password).md5()

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01233456789"
        return StringBuilder().apply {
            repeat(6) {
                (possible.indices).random().also {index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray()) //16byte
        val hexString = BigInteger(1, digest). toString(16)
        return hexString.padStart(32, '0')
    }

    private fun sendAccessCodeToUser(phone: String?, code: String) {
        println("..... sending access code: $code on $phone")
    }

    companion object Factory {
        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone: String? = null
        ):User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !phone.isNullOrBlank() -> User(firstName, lastName, phone)
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(firstName, lastName, email, password)
                else -> throw IllegalArgumentException("Email or phone must be not null or blank")
            }
        }

        fun importUser(
            fullName: String? = null,
            email: String?  = null,
            passwordHash: String? = null,
            salt: String? = null,
            phone: String? = null
        ):User {
            val (firstName, lastName) = (fullName?:"").fullNameToPair()

            val user = User(firstName, lastName, email, phone, meta = mapOf("src" to "csv"))
            if (!passwordHash.isNullOrBlank() && !salt.isNullOrBlank()) {
                user.passwordHash = passwordHash
                user.salt = salt
            }
            return user
        }

        private fun String.fullNameToPair() : Pair<String, String?> {
            return this.split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when(size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else ->  throw IllegalArgumentException("Fullname must be contail only first name " +
                                "and last name, current split result ${this@fullNameToPair}")
                    }
                }
        }
    }
}
