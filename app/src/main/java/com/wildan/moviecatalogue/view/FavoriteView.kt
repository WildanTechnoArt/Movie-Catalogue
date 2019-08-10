package com.wildan.moviecatalogue.view

import android.content.Context
import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.database.TvShowEntity
import com.wildan.moviecatalogue.model.favorite.Favorite

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

    interface FavoritePresenter {
        fun insertFavorite(context: Context, movie: Favorite)
        fun deleteFavorite(context: Context, movie: Favorite)
        fun onDestroy()
    }
}