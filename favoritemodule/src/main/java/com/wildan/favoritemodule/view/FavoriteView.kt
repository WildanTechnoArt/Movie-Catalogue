package com.wildan.favoritemodule.view

import android.content.Context
import com.wildan.favoritemodule.model.Movie

class FavoriteView {

    interface View{
        fun showFavoriteMovie(movie: List<Movie>)
        fun handleError(throwable: String?)
    }

    interface Presenter{
        fun fetchMoviesData(context: Context)
        fun onDestroy()
    }
}