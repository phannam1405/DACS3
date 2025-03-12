package com.example.dacs3

import com.cloudinary.Cloudinary


object CloudinaryHelper {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "d1lexacvx",
            "api_key" to "813933897632645",
            "api_secret" to "pKN1zj1ziLlY-85X4QKAQpySVPg"
        )
    )

    fun getCloudinary(): Cloudinary {
        return cloudinary
    }
}
