package com.wildan.moviecatalogue.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
    var poster: Int? = null,
    var title: String? = null,
    var date: String? = null,
    var rating: String? = null,
    var genres: String? = null,
    var description: String? = null
) : Parcelable