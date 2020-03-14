package ru.skillbranch.kotlinexample

object UserHolder {
    private val map = mutableMapOf<String, User>()
    fun regitstredUser (
        fullName:String,
        email:String,
        passwrd:String
    ):User {
        return User.makeUser(fullName, email, passwrd)
            .also { user-> map[user.login] = user}
    }

    fun loginUser (login:String, password: String): String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }
}