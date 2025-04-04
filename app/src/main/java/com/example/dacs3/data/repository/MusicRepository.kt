package com.example.dacs3.data.repository

import android.app.Application
import com.example.dacs3.data.database.MusicDatabase
import com.example.dacs3.data.model.Music

class MusicRepository(var application: Application) {

    private lateinit var db: MusicDatabase

    init {
        db = MusicDatabase.getInstance(application)
    }

    fun insertMusic(course: Music?) {
        db.myDao().insert(course)
    }

    fun update(course: Music?) {
        db.myDao().update(course)
    }

    fun deleteMusic(course: Music?): Int {
        return db.myDao().delete(course)
    }

    fun deleteAll() {
        db.myDao().deleteAllCourses()
    }

    fun getAll(): List<Music?>? {
        return db.myDao().getAllSong
    }
}