package com.wildan.moviecatalogue.view

class CreditView {

    interface View {
        fun showProgressBar()
        fun hideProgressBar()
        fun handleError(t: Throwable?)
        fun onSuccess()
    }

    interface ViewModel {
        fun setCreditData(apiKey: String, movieId: String, view: View)
        fun onDestroy()
    }
}