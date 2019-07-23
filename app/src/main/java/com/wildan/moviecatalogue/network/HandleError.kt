package com.wildan.moviecatalogue.network

import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import retrofit2.HttpException

class NetworkError {

    companion object {
        fun handleError(e: HttpException, context: Context?) {
            val gson = Gson()
            val response = gson.fromJson(e.response().errorBody()?.charStream(), ErrorResponse::class.java)
            val message = response.statusMessage.toString()
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}