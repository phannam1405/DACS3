package com.example.dacs3.data.model

import java.io.Serializable

data class DataSongList(
    var id: String? = null,
    val song_name: String? = null,
    val singer_name: String? = null,
    val image: String? = null,
    val singer_image: String? = null,
    val audio: String? = null,
    val cate: String? = null
) : Serializable
