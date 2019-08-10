package com.wildan.moviecatalogue.model.credit

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CrewData (

    @SerializedName("department")
    @Expose
    var department: String? = null,

    @SerializedName("job")
    @Expose
    var job: String? = null,

    @SerializedName("name")
    @Expose
    var name: String? = null,

    @SerializedName("profile_path")
    @Expose
    var profilePath: String? = null

)