package com.wildan.moviecatalogue.view

class RecommendedView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
        fun onSuccess()
    }

    interface ViewModel {
        fun setRecommended(page: Int, apiKey: String, movieId: String, view: View)
        fun onDestroy()
    }

}