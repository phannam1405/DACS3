package com.example.dacs3

import com.cloudinary.Cloudinary


object CloudinaryHelper {
    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "drqbcsyqj",
            "api_key" to "427458218633888",
            "api_secret" to "XpLnpFWk_PdP_gAe9FDiewY49_I"
        )
    )

    fun getCloudinary(): Cloudinary {
        return cloudinary
    }
}
