package com.example.pillclassificationkeras.user

object User {
    private val user: UserProfile = UserProfile()

    fun getUser(): UserProfile {
        return user
    }
}