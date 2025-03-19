package com.example.dacs3


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MusicDao {

    //Annotation @Insert giúp Room hiểu rằng đây là câu lệnh INSERT INTO trong SQL
    @Insert
    fun insert(model: Music?)
    @Update
    fun update(model: Music?)

    @Delete
    fun delete(model: Music?)

    @Query("DELETE FROM music_table")
    fun deleteAllCourses()

    @get:Query("SELECT * FROM music_table")
    val getAllSong: List<Music>

}