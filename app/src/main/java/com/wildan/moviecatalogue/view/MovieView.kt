package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.repository.MovieRepositoryImp

class MovieView {

    interface View {
        fun getMovieData(movie: MovieResponse)
        fun noInternetConnection(message: String)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun setMovie(
            apiKey: String,
            page: Int,
            language: String,
            view: View,
            movie: MovieRepositoryImp
        )

        fun onDestroy()
    }
}