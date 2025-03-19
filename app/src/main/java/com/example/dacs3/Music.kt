package com.example.dacs3

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "music_table")
class Music(songName: String, coverImage: String, localAudioPath: String, singerImage:String, cate:String, singer_name:String) {

    @PrimaryKey(autoGenerate = true) var id: Int = 0
    val songName: String=songName
    val coverImage: String=coverImage
    val cate:String = cate
    val localAudioPath: String=localAudioPath
    val singerImage: String=singerImage
    val singer_name:String = singer_name
}
