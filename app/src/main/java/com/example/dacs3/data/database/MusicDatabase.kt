package com.example.dacs3.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dacs3.data.model.Music

@Database(entities = arrayOf(Music::class), version = 1, exportSchema = false)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun myDao(): MusicDao

    //companion object → Dùng để tạo một biến INSTANCE có thể được truy cập từ bất kỳ đâu mà không cần khởi tạo đối tượng CourseDatabase.
    companion object {
        //@Volatile → Đảm bảo mọi thay đổi của INSTANCE sẽ được nhìn thấy ngay lập tức bởi tất cả các thread (tránh lỗi multi-threading)
        @Volatile


        //INSTANCE ở đây là 1 database
        private var INSTANCE: MusicDatabase? = null

        fun getInstance(context: Context): MusicDatabase {
            //Kiểm tra INSTANCE có tồn tại không
            //Nếu đã có database → Trả về ngay
            //Nếu chưa có → Chạy synchronized {} để đảm bảo chỉ có một thread tạo database.
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java, "DACS3" // Tên của database.
                ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}