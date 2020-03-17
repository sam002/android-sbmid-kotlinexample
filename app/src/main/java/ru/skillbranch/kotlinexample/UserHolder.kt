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

    fun importUsers(list: List<String>): List<User> {
        val resultStrings = emptyList<User>().toMutableList()
        list.forEach {nodeString ->
            nodeString.split(";")
                .apply {
                    var password: String
                    var salt:String
                    this.getOrNull(2)?.split(":".toRegex()).let {
                        salt = first()
                        password = last()
                    }
                    val userMap = hashMapOf(
                        "fullName" to this.getOrNull(0),
                        "email" to this.getOrNull(1),
                        "phone" to this.getOrNull(3)
                    )

                    val fullName:String? = if(userMap["fullName"].isNullOrEmpty()) null else userMap["fullName"]
                    val email:String? = if(userMap["email"].isNullOrEmpty()) null else userMap["email"]
                    val phone:String? = if(userMap["phone"].isNullOrEmpty()) null else userMap["phone"]

                    val impordedUser = User.importUser(
                        fullName,
                        email,
                        password,
                        salt,
                        phone
                    )

                    map[impordedUser.login] = impordedUser
                    resultStrings += impordedUser
                }
        }

        return resultStrings
    }
}