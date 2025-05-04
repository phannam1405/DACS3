package com.example.dacs3.data.model

import java.io.Serializable

data class DataUser(
    var id: String? = null,
    val userName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val des: String = "",
    val avatarUrl: String = ""
): Serializable