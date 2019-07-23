package com.wildan.moviecatalogue.model.movie

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wildan.moviecatalogue.model.Genre
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailMovieResponse(

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("vote_average")
    @Expose
    var voteAverage: String? = null,

    @SerializedName("release_date")
    @Expose
    var releaseDate: String? = null,

    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null,

    @SerializedName("overview")
    @Expose
    var overview: String? = null,

    @SerializedName("popularity")
    @Expose
    var popularity: String? = null,

    @SerializedName("genres")
    @Expose
    var genreList: List<Genre> = emptyList(),

    @SerializedName("runtime")
    @Expose
    var runtime: String? = null

): Parcelable