package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.MovieView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieViewModel : ViewModel(), MovieView.ViewModel {

    private val listMovies = MutableLiveData<ArrayList<MovieResult>>()
    private val compositeDisposable = CompositeDisposable()

    fun getMovies(): LiveData<ArrayList<MovieResult>> {
        return listMovies
    }

    override fun setMovie(apiKey: String, page: Int, language: String, view: MovieView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "discover/movie")
            .addQueryParameter("api_key", apiKey)
            .addQueryParameter("page", page.toString())
            .addQueryParameter("language", language)
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

    override fun onDestroy() {
        compositeDisposable.dispose()
    }
}