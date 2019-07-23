package com.wildan.moviecatalogue.network

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ErrorResponse(

    @SerializedName("status_code")
    @Expose
    var statusCode: Int? = null,

    @SerializedName("status_message")
    @Expose
    var statusMessage: String? = null,

    @SerializedName("success")
    @Expose
    var success: String? = null
)