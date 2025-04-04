package com.example.dacs3.data.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.dacs3.data.model.Music

@Dao
interface MusicDao {
    @Insert
    fun insert(model: Music?)

    @Update
    fun update(model: Music?)

    @Delete
    fun delete(model: Music?): Int

    @Query("DELETE FROM music_table")
    fun deleteAllCourses()

    @get:Query("SELECT * FROM music_table")
    val getAllSong: List<Music>

}