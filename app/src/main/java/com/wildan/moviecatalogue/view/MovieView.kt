package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.movie.MovieResponse

class MovieView {

    interface View {
        fun getMovieData(movie: MovieResponse)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun setMovie(apiKey: String, page: Int, language: String, view: View)
        fun onDestroy()
    }
}