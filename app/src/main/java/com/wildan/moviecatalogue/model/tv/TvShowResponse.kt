package com.wildan.moviecatalogue.model.tv

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TvShowResponse(

    @SerializedName("page")
    @Expose
    var page: Int? = null,

    @SerializedName("total_results")
    @Expose
    var totalResults: Int? = null,

    @SerializedName("total_pages")
    @Expose
    var totalPages: Int? = null,

    @SerializedName("results")
    @Expose
    var tvResult: ArrayList<TvShowResult>

) : Parcelable