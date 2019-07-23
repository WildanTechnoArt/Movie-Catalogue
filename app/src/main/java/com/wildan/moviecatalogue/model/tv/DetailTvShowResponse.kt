package com.wildan.moviecatalogue.model.tv

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.wildan.moviecatalogue.model.Genre
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DetailTvShowResponse(

    @SerializedName("backdrop_path")
    @Expose
    var backdropPath: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("vote_average")
    @Expose
    var voteAverage: String? = null,

    @SerializedName("first_air_date")
    @Expose
    var firstAirDate: String? = null,

    @SerializedName("poster_path")
    @Expose
    var posterPath: String? = null,

    @SerializedName("popularity")
    @Expose
    var popularity: String? = null,

    @SerializedName("overview")
    @Expose
    var overview: String? = null,

    @SerializedName("genres")
    @Expose
    var genreList: List<Genre> = emptyList()

): Parcelable