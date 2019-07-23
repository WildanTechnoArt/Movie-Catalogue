package com.wildan.moviecatalogue.view

import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.database.TvShowEntity

class FavoriteView {

    interface MoviePresenter {
        fun insertMovie(movie: MovieEntity)
        fun deleteMovie(movieId: String)
        fun onDestroy()
    }

    interface TvPresenter {
        fun insertTvShow(tv: TvShowEntity)
        fun deleteTvShow(tvId: String)
        fun onDestroy()
    }
}