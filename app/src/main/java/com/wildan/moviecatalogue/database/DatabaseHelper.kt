package com.wildan.moviecatalogue.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper internal constructor(context: Context) :
    SQLiteOpenHelper(context,
        DATABASE_NAME, null,
        DATABASE_VERSION
    ) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + DatabaseContract.TABLE_NAME)
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "db_favorite_movie"
        private const val DATABASE_VERSION = 12
        private val SQL_CREATE_TABLE = String.format(
            "CREATE TABLE %s" +
                    "(%s INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "%s INTEGER NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "%s TEXT NOT NULL," +
                    "UNIQUE (%s) ON CONFLICT REPLACE)",
            DatabaseContract.TABLE_NAME,
            DatabaseContract.MovieColumns.ID,
            DatabaseContract.MovieColumns.MOVIE_ID,
            DatabaseContract.MovieColumns.TITLE,
            DatabaseContract.MovieColumns.DATE,
            DatabaseContract.MovieColumns.POSTER,
            DatabaseContract.MovieColumns.POPULARITY,
            DatabaseContract.MovieColumns.RATING,
            DatabaseContract.MovieColumns.MOVIE_ID
        )
    }
}