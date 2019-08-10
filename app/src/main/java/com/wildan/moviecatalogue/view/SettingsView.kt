package com.wildan.moviecatalogue.view

import android.content.Context
import com.wildan.moviecatalogue.model.movie.MovieResult

class SettingsView {

    interface View {
        fun setAlarm(movie: ArrayList<MovieResult>)
        fun onSuccess(context: Context, appName: String?)
        fun handleError(e: Throwable, context: Context)
    }

    interface Presenter {
        fun setRepeatingAlarm(context: Context, appName: String?)
    }
}