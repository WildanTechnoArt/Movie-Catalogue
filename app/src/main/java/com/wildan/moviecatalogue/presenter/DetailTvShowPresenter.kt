package com.wildan.moviecatalogue.presenter

import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.view.DetailTvShowView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

class DetailTvShowPresenter(
    private val view: DetailTvShowView.View,
    private val movie: MovieRepositoryImp?
) : DetailTvShowView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailTvShow(apiKey: String, tvId: String, language: String) {
        view.showProgressBar()

        movie?.getDetailTvShow(apiKey, tvId, language)
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribeOn(Schedulers.io())
            ?.subscribeWith(object : ResourceSubscriber<DetailTvShowResponse>() {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onNext(t: DetailTvShowResponse?) {
                    t?.let { view.showDetailTvShow(it) }
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