package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.movie.DetailMovieResponse

class DetailMovieView {

    interface View {
        fun showDetailMovie(movie: DetailMovieResponse)
        fun noInternetConnection(message: String)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
        fun onSuccess()
    }

    interface Presenter {
        fun getDetailMovie(apiKey: String, movieId: String, language: String)
        fun onDestroy()
    }
}