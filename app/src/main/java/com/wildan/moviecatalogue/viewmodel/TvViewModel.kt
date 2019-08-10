package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.utils.UtilsConstant
import com.wildan.moviecatalogue.view.TvShowView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class TvViewModel : ViewModel(), TvShowView.ViewModel {

    private val listTvShow = MutableLiveData<ArrayList<TvShowResult>>()
    private val compositeDisposable = CompositeDisposable()

    fun getTvShows(): LiveData<ArrayList<TvShowResult>> {
        return listTvShow
    }

    override fun setTvShow(apiKey: String, page: Int, language: String, view: TvShowView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(UtilsConstant.BASE_URL + "discover/tv")
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("language", language)
            .build()
            .getObjectObservable(TvShowResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TvShowResponse> {
                override fun onComplete() {
                    view.hideProgressBar()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: TvShowResponse) {
                    t.let { view.getTvShowData(it) }
                    listTvShow.postValue(t.tvResult)
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