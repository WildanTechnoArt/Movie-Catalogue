package com.wildan.moviecatalogue.utils

import com.wildan.moviecatalogue.BuildConfig

class UtilsConstant {

    companion object{
        const val BASE_URL: String = BuildConfig.BASE_URL
        const val POSTER_URL: String = BuildConfig.POSTER_URL
        const val BACKDROP_URL: String = BuildConfig.BACKDROP_URL
        const val API_KEY: String = BuildConfig.API_KEY
        const val MOVIE_EXTRA: String = "movie_data"
        const val STATE_SAVED: String = "state_saved"

        const val SAVE_TITLE: String = "save_title"
        const val SAVE_DATE: String = "save_date"
        const val SAVE_RATING: String = "save_rating"
        const val SAVE_POSTER: String = "save_poster"
        const val SAVE_BACKDROP: String = "save_backdrop"
        const val SAVE_DURATION: String = "save_duration"
        const val SAVE_GENRES: String = "save_genres"
        const val SAVE_OVERVIEW: String = "save_overwiew"
    }
}