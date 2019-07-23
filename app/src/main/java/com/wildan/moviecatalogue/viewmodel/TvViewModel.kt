package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.view.TvShowView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

class TvViewModel : ViewModel(), TvShowView.ViewModel {

    private val listTvShow = MutableLiveData<ArrayList<TvShowResult>>()
    private val compositeDisposable = CompositeDisposable()

    fun getTvShows(): LiveData<ArrayList<TvShowResult>> {
        return listTvShow
    }

    override fun setTvShow(
        apiKey: String,
        page: Int,
        language: String,
        view: TvShowView.View,
        tv: MovieRepositoryImp
    ) {

        view.showProgressBar()

        compositeDisposable.add(
            tv.getOnTheAirMovie(apiKey, page, language)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ResourceSubscriber<TvShowResponse>() {
                    override fun onComplete() {
                        view.hideProgressBar()
                    }

                    override fun onNext(t: TvShowResponse?) {
                        t?.let { view.getTvShowData(it) }
                        listTvShow.postValue(t?.movieResult)
                    }

                    override fun onError(t: Throwable?) {
                        view.hideProgressBar()
                        view.handleError(t)
                    }

                })
        )

    }

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}