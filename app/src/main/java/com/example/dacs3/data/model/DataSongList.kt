package com.example.dacs3.data.model

import java.io.Serializable

data class DataSongList(
    var id: String? = null,
    val songName: String? = null,
    val singerName: String? = null,
    val image: String? = null,
    val singerImage: String? = null,
    val audio: String? = null,
    val category: String? = null,
    val view: Long =0
) : Serializable
