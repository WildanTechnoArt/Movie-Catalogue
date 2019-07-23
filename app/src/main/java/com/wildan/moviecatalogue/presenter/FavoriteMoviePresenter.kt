package com.wildan.moviecatalogue.presenter

import androidx.lifecycle.ViewModel
import com.wildan.moviecatalogue.database.MovieDao
import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.view.FavoriteView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoriteMoviePresenter(private val movieDao: MovieDao) : ViewModel(), FavoriteView.MoviePresenter {

    private val compositeDisposable = CompositeDisposable()

    override fun insertMovie(movie: MovieEntity) {
        compositeDisposable.add(Observable.fromCallable { movieDao.insertMovie(movie) }
            .subscribeOn(Schedulers.computation())
            .subscribe())
    }

    override fun deleteMovie(movieId: String) {
        compositeDisposable.add(Observable.fromCallable { movieDao.deleteMovie(movieId) }
            .subscribeOn(Schedulers.computation())
            .subscribe())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}