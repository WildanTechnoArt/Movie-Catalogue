package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.model.tv.TvShowResponse

class SearchListView {

    interface View {
        fun getTvShowData(tv: TvShowResponse)
        fun getMovieData(movie: MovieResponse)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun searchMovie(apiKey: String, query: String?, page: Int, language: String, view: View)
        fun searchTvShow(apiKey: String, query: String?, page: Int, language: String, view: View)
        fun onDestroy()
    }
}