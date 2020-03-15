package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting

object UserHolder {
    private val map = mutableMapOf<String, User>()
    fun registerUser (
        fullName:String,
        email:String,
        passwrd:String
    ):User {
        return User.makeUser(fullName, email, passwrd)
            .also { user->
                if (user.login in map.keys) {
                    throw IllegalArgumentException("A user with this email already exists")
                }
                map[user.login] = user
            }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ):User {
        return User.makeUser(fullName, phone = rawPhone)
            .also { user->
                if (user.login in map.keys) {
                    throw IllegalArgumentException("A user with this phone already exists")
                }
                map[user.login] = user
                map[rawPhone] = user
            }
    }

    fun requestAccessCode(login: String):Unit {
        map[login.trim()]?.requestAccessCode()
    }

    fun loginUser (login:String, password: String): String? {
//        println("S_UserHolder: ${login.trim()} IN ${map.keys}")
        return map[login.trim()]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun importUsers(list: List<String>): List<String> {
        val resultStrings = emptyList<String>().toMutableList()
        list.forEach {nodeString ->
            nodeString.split(";")
                .filter { it.isNotBlank() }
                .apply {
                    val (salt, pass) = this[2].split(':')
                    val userMap = hashMapOf(
                        "fullName" to this[0],
                        "email" to this[1],
                        "password" to pass,
                        "salt" to salt,
                        "phone" to this[3]
                    )

                    val impordedUser = User.importUser(
                        userMap["fullName"]!!,
                        userMap["email"]!!,
                        userMap["password"]!!,
                        userMap["salt"]!!,
                        userMap["phone"]!!
                    )

                    map[impordedUser.login] = impordedUser
                    resultStrings += impordedUser.userInfo
                }
        }

        return resultStrings
    }
}