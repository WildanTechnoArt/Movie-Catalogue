package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.SearchListView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel : ViewModel(), SearchListView.ViewModel {

    private val listMovies = MutableLiveData<ArrayList<MovieResult>>()
    private val listTvShow = MutableLiveData<ArrayList<TvShowResult>>()

    private val compositeDisposable = CompositeDisposable()

    fun getMovies(): LiveData<ArrayList<MovieResult>> {
        return listMovies
    }

    fun getTvShows(): LiveData<ArrayList<TvShowResult>> {
        return listTvShow
    }

    override fun searchMovie(apiKey: String, query: String?, page: Int, language: String, view: SearchListView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "search/movie")
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("language", language)
            .addQueryParameter("query", query)
            .addQueryParameter("page", page.toString())
            .build()
            .getObjectObservable(MovieResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieResponse> {
                override fun onComplete() {
                    view.hideProgressBar()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: MovieResponse) {
                    t.let { view.getMovieData(it) }
                    listMovies.postValue(t.movieResult)
                }

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }

            })
    }

    override fun searchTvShow(apiKey: String, query: String?, page: Int, language: String, view: SearchListView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "search/tv")
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("query", query)
            .addQueryParameter("language", language)
            .addQueryParameter("page", page.toString())
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