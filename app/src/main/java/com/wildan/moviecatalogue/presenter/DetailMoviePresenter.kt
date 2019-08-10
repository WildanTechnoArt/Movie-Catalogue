package com.wildan.moviecatalogue.presenter

import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.DetailMovieView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailMoviePresenter(
    private val view: DetailMovieView.View
) : DetailMovieView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailMovie(apiKey: String, movieId: String, language: String) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "movie/{movie_id}")
            .addPathParameter("movie_id", movieId)
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("language", language)
            .build()
            .getObjectObservable(DetailMovieResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<DetailMovieResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: DetailMovieResponse) {
                    t.let { view.showDetailMovie(it) }
                }

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }
            })
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}