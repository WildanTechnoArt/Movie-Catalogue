package com.wildan.moviecatalogue.model.credit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CastData (

    @SerializedName("character")
    @Expose
    var character: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("profile_path")
    @Expose
    var profilePath: String? = null

)