package com.wildan.favoritemodule.presenter

import android.content.Context
import com.wildan.favoritemodule.database.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.wildan.favoritemodule.model.Movie
import com.wildan.favoritemodule.view.FavoriteView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoriteMoviePresenter(private val view: FavoriteView.View) : FavoriteView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun fetchMoviesData(context: Context) {
        compositeDisposable.add(
            getMovieData(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({ movies ->
                    view.showFavoriteMovie(movies)
                }, {throwable ->
                    view.handleError(throwable.localizedMessage)
                })
        )
    }

    private fun getMovieData(context: Context): Observable<List<Movie>> {
        return Observable.create { e ->
            val movies = arrayListOf<Movie>()
            val cursor = context.contentResolver.query(CONTENT_URI, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                do {
                    movies.add(Movie(cursor))
                } while (cursor.moveToNext())
            }

            cursor?.close()
            e.onNext(movies)
        }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}