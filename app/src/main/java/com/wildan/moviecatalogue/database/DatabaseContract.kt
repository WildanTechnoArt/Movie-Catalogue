package com.wildan.moviecatalogue.database

import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.wildan.moviecatalogue"
    private const val SCHEME = "content"
    const val TABLE_NAME = "favorite_movie"

    class MovieColumns : BaseColumns {
        companion object {
            const val ID = "id"
            const val MOVIE_ID = "movie_id"
            const val TITLE = "title"
            const val DATE = "data"
            const val POSTER = "image_poster"
            const val POPULARITY = "popularity"
            const val RATING = "rating"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

    fun getColumnString(cursor: Cursor, columnName: String): String {
        return cursor.getString(cursor.getColumnIndex(columnName))
    }

    fun getColumnInt(cursor: Cursor, columnName: String): Int {
        return cursor.getInt(cursor.getColumnIndex(columnName))
    }
}