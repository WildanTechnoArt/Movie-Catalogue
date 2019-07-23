package com.wildan.moviecatalogue.database

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "tTvShow")
data class TvShowEntity(

    @NonNull
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "tvId")
    var tvId: String? = null,

    @ColumnInfo(name = "tv_title")
    var tvName: String? = null,

    @ColumnInfo(name = "tv_date")
    var tvDate: String? = null,

    @ColumnInfo(name = "tv_rating")
    var tvRating: String? = null,

    @ColumnInfo(name = "tv_poster")
    var tvPoster: String? = null,

    @ColumnInfo(name = "tv_popularity")
    var tvPopularity: String? = null,

    @ColumnInfo(name = "tv_overview")
    var tvOverview: String? = null,

    @ColumnInfo(name = "tv_genre")
    var tvGenre: String? = null,

    @ColumnInfo(name = "tv_backdrop")
    var tvBackdrop: String? = null

) : Parcelable