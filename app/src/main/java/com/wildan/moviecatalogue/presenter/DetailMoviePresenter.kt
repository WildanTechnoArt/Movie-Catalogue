package com.wildan.moviecatalogue.presenter

import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.view.DetailMovieView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

class DetailMoviePresenter(
    private val view: DetailMovieView.View,
    private val movie: MovieRepositoryImp?
) : DetailMovieView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailMovie(apiKey: String, movieId: String, language: String) {
        view.showProgressBar()

        movie?.getDetailMovie(apiKey, movieId, language)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribeWith(object : ResourceSubscriber<DetailMovieResponse>() {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onNext(t: DetailMovieResponse?) {
                    t?.let { view.showDetailMovie(it) }
                }

                override fun onError(t: Throwable?) {
                    view.hideProgressBar()
                    view.handleError(t)
                }

            })?.let {
                compositeDisposable.add(
                    it
                )
            }
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}