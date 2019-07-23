package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.view.MovieView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber

class MovieViewModel : ViewModel(), MovieView.ViewModel {

    private val listMovies = MutableLiveData<ArrayList<MovieResult>>()
    private val compositeDisposable = CompositeDisposable()

    fun getMovies(): LiveData<ArrayList<MovieResult>> {
        return listMovies
    }

    override fun setMovie(
        apiKey: String,
        page: Int,
        language: String,
        view: MovieView.View,
        movie: MovieRepositoryImp
    ) {

        view.showProgressBar()

        compositeDisposable.add(
            movie.getMovieData(apiKey, page, language)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(object : ResourceSubscriber<MovieResponse>() {
                    override fun onComplete() {
                        view.hideProgressBar()
                    }

                    override fun onNext(t: MovieResponse?) {
                        t?.let { view.getMovieData(it) }
                        listMovies.postValue(t?.movieResult)
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