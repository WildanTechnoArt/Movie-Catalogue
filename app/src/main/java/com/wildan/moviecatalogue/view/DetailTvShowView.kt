package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse

class DetailTvShowView {

    interface View {
        fun showDetailTvShow(tv: DetailTvShowResponse)
        fun noInternetConnection(message: String)
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
        fun onSuccess()
    }

    interface Presenter {
        fun getDetailTvShow(apiKey: String, tvId: String, language: String)
        fun onDestroy()
    }
}