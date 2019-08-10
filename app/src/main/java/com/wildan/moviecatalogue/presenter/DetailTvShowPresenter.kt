package com.wildan.moviecatalogue.presenter

import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.DetailTvShowView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class DetailTvShowPresenter(
    private val view: DetailTvShowView.View
) : DetailTvShowView.Presenter {

    private val compositeDisposable = CompositeDisposable()

    override fun getDetailTvShow(apiKey: String, tvId: String, language: String) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "tv/{tv_id}")
            .addPathParameter("tv_id", tvId)
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("language", language)
            .build()
            .getObjectObservable(DetailTvShowResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<DetailTvShowResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: DetailTvShowResponse) {
                    t.let { view.showDetailTvShow(it) }
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