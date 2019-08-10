package com.wildan.moviecatalogue.presenter

import android.content.ContentValues
import android.content.Context
import com.wildan.moviecatalogue.model.favorite.Favorite
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.DATE
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.MOVIE_ID
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.POPULARITY
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.POSTER
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.RATING
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.TITLE
import com.wildan.moviecatalogue.view.FavoriteView
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoritePresenter : FavoriteView.FavoritePresenter {

    private val compositeDisposable = CompositeDisposable()

    override fun insertFavorite(context: Context, movie: Favorite) {
        compositeDisposable.add(Completable.create { e ->
            context.contentResolver.insert(
                CONTENT_URI,
                setFavoriteContentValues(movie)
            )
            e.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    override fun deleteFavorite(context: Context, movie: Favorite) {
        val whereClause = String.format("%s = ?", MOVIE_ID)
        val args = arrayOf(movie.id.toString())

        compositeDisposable.add(Completable.create { e ->
            context.contentResolver.delete(
                CONTENT_URI,
                whereClause,
                args
            )
            e.onComplete()
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }

    companion object {
        fun setFavoriteContentValues(movie: Favorite): ContentValues {
            val values = ContentValues()
            values.put(MOVIE_ID, movie.id)
            values.put(TITLE, movie.title.toString())
            values.put(DATE, movie.releaseDate.toString())
            values.put(POSTER, movie.posterPath.toString())
            values.put(POPULARITY, movie.popularity.toString())
            values.put(RATING, movie.voteAverage.toString())
            return values
        }
    }
}