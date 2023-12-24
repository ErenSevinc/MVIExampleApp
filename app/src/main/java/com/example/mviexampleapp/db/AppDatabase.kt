package com.example.mviexampleapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mviexampleapp.model.Articles


@Database(
    entities = [Articles::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): ArticleDao
}