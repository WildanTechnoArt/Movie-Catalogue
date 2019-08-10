package com.wildan.moviecatalogue.model.credit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreditResponse (

    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("cast")
    @Expose
    var cast: ArrayList<CastData>? = null,

    @SerializedName("crew")
    @Expose
    var crew: ArrayList<CrewData>? = null

)