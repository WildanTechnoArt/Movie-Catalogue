package com.wildan.moviecatalogue.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.credit.CastData
import com.wildan.moviecatalogue.model.credit.CreditResponse
import com.wildan.moviecatalogue.model.credit.CrewData
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.CreditView
import com.wildan.moviecatalogue.view.RecommendedView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MovieCreditViewModel : ViewModel(), CreditView.ViewModel, RecommendedView.ViewModel {

    private val listCasts = MutableLiveData<ArrayList<CastData>>()
    private val listCrews = MutableLiveData<ArrayList<CrewData>>()
    private val listRecommend = MutableLiveData<ArrayList<MovieResult>>()
    private val compositeDisposable = CompositeDisposable()

    fun getCastList(): LiveData<ArrayList<CastData>> {
        return listCasts
    }

    fun getCrewList(): LiveData<ArrayList<CrewData>> {
        return listCrews
    }

    fun getListRecommend(): LiveData<ArrayList<MovieResult>> {
        return listRecommend
    }

    override fun setCreditData(apiKey: String, movieId: String, view: CreditView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "movie/{movie_id}/credits")
            .addQueryParameter("api_key", apiKey)
            .addPathParameter("movie_id", movieId)
            .build()
            .getObjectObservable(CreditResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CreditResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: CreditResponse) {
                    listCasts.postValue(t.cast)
                    listCrews.postValue(t.crew)
                }

                override fun onError(e: Throwable) {
                    view.hideProgressBar()
                    view.handleError(e)
                }

            })
    }

    override fun setRecommended(page: Int, apiKey: String, movieId: String, view: RecommendedView.View) {
        view.showProgressBar()

        Rx2AndroidNetworking.get(BASE_URL + "movie/{movie_id}/recommendations")
            .addQueryParameter("api_key", apiKey)
            .addPathParameter("movie_id", movieId)
            .addQueryParameter("page", page.toString())
            .build()
            .getObjectObservable(MovieResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieResponse> {
                override fun onComplete() {
                    view.onSuccess()
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: MovieResponse) {
                    listRecommend.postValue(t.movieResult)
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