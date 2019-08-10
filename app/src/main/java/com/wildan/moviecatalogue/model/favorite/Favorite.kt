package com.wildan.moviecatalogue.model.favorite

import android.database.Cursor
import android.os.Parcelable
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.DATE
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.MOVIE_ID
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.POPULARITY
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.POSTER
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.RATING
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.TITLE
import com.wildan.moviecatalogue.database.DatabaseContract.getColumnInt
import com.wildan.moviecatalogue.database.DatabaseContract.getColumnString
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Favorite(
    var id: Int? = null,
    var title: String? = null,
    var voteAverage: String? = null,
    var releaseDate: String? = null,
    var posterPath: String? = null,
    var popularity: String? = null

) : Parcelable {
    constructor(cursor: Cursor) : this() {
        this.id = getColumnInt(cursor, MOVIE_ID)
        this.title = getColumnString(cursor, TITLE)
        this.voteAverage = getColumnString(cursor, RATING)
        this.releaseDate = getColumnString(cursor, DATE)
        this.popularity = getColumnString(cursor, POPULARITY)
        this.posterPath = getColumnString(cursor, POSTER)
    }
}