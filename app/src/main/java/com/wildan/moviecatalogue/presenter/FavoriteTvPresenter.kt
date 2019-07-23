package com.wildan.moviecatalogue.presenter

import androidx.lifecycle.ViewModel
import com.wildan.moviecatalogue.database.TvShowDao
import com.wildan.moviecatalogue.database.TvShowEntity
import com.wildan.moviecatalogue.view.FavoriteView
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class FavoriteTvPresenter(private val tvDao: TvShowDao) : ViewModel(), FavoriteView.TvPresenter {

    private val compositeDisposable = CompositeDisposable()

    override fun insertTvShow(tv: TvShowEntity) {
        compositeDisposable.add(Observable.fromCallable { tvDao.insertTvShow(tv) }
            .subscribeOn(Schedulers.computation())
            .subscribe())
    }

    override fun deleteTvShow(tvId: String) {
        compositeDisposable.add(Observable.fromCallable { tvDao.deleteTvShow(tvId) }
            .subscribeOn(Schedulers.computation())
            .subscribe())
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}