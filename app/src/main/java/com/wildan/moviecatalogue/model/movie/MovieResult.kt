package com.wildan.moviecatalogue.model.movie

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieResult(

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("vote_average")
    @Expose
    var voteAverage: String? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null,

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null,

    @SerializedName("overview")
    @Expose
    var overview: String? = null,

    @SerializedName("release_date")
    @Expose
    var releaseDate: String? = null,

    @SerializedName("popularity")
    @Expose
    var popularity: String? = null

) : Parcelable