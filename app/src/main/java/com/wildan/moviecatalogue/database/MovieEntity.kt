package com.wildan.moviecatalogue.database

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "tMovie")
data class MovieEntity(

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "movieId")
    var movieId: String? = null,

    @ColumnInfo(name = "movie_title")
    var movieName: String? = null,

    @ColumnInfo(name = "movie_date")
    var movieDate: String? = null,

    @ColumnInfo(name = "movie_rating")
    var movieRating: String? = null,

    @ColumnInfo(name = "movie_poster")
    var moviePoster: String? = null,

    @ColumnInfo(name = "movie_popularity")
    var moviePopularity: String? = null,

    @ColumnInfo(name = "movie_overview")
    var movieOverview: String? = null,

    @ColumnInfo(name = "movie_genre")
    var movieGenre: String? = null,

    @ColumnInfo(name = "movie_duration")
    var movieDuration: String? = null,

    @ColumnInfo(name = "movie_backdrop")
    var movieBackdrop: String? = null

) : Parcelable