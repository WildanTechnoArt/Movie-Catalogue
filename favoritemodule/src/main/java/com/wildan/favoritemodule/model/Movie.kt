package com.wildan.favoritemodule.model

import android.database.Cursor
import android.os.Parcelable
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.DATE
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.MOVIE_ID
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.POPULARITY
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.POSTER
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.RATING
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.TITLE
import com.wildan.favoritemodule.database.DatabaseContract.getColumnInt
import com.wildan.favoritemodule.database.DatabaseContract.getColumnString
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    var id: Int? = null,
    var title: String? = null,
    var voteAverage: String? = null,
    var releaseDate: String? = null,
    var posterPath: String? = null,
    var popularity: String? = null

) : Parcelable {
    constructor(cursor: Cursor?) : this() {
        this.id = getColumnInt(cursor, MOVIE_ID)
        this.title = getColumnString(cursor, TITLE)
        this.voteAverage = getColumnString(cursor, RATING)
        this.releaseDate = getColumnString(cursor, DATE)
        this.popularity = getColumnString(cursor, POPULARITY)
        this.posterPath = getColumnString(cursor, POSTER)
    }
}