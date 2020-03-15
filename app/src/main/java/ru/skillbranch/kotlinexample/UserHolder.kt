package ru.skillbranch.kotlinexample

import android.util.Log
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
}