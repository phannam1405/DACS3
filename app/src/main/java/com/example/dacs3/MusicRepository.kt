package com.example.dacs3

import android.app.Application

class MusicRepository(var application: Application) {

    private lateinit var db: MusicDatabase //db là biến chứa database.

    //init {} để khởi tạo database hoac lấy ra db đó
    init {
        db = MusicDatabase.getInstance(application)
    }

    fun insert(course: Music?) {
        db.myDao().insert(course)
    }

    fun update(course: Music?) {
        db.myDao().update(course)
    }

    fun delete(course: Music?) {
        db.myDao().delete(course)
    }

    fun deleteAll() {
        db.myDao().deleteAllCourses()
    }

    fun getAll(): List<Music?>? {
        return db.myDao().getAllSong
    }
}