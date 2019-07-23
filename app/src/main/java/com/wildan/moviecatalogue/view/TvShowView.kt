package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.repository.MovieRepositoryImp

class TvShowView {

    interface View {
        fun getTvShowData(tv: TvShowResponse)
        fun noInternetConnection(message: String)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
    }

    interface ViewModel {
        fun setTvShow(
            apiKey: String,
            page: Int,
            language: String,
            view: View,
            tv: MovieRepositoryImp
        )

        fun onDestroy()
    }
}