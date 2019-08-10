package com.wildan.moviecatalogue.presenter

import android.content.Context
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BASE_URL
import com.wildan.moviecatalogue.view.SettingsView
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class SettingsPresenter(private val view: SettingsView.View) : SettingsView.Presenter {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val date = Date()
    private val currentDate = dateFormat.format(date)

    private val compositeDisposable = CompositeDisposable()

    override fun setRepeatingAlarm(context: Context, appName: String?) {
        Rx2AndroidNetworking.get(BASE_URL + "discover/movie")
            .addQueryParameter("primary_release_date.gte", currentDate)
            .addQueryParameter("primary_release_date.lte", currentDate)
            .addQueryParameter("api_key", API_KEY)
            .build()
            .getObjectObservable(MovieResponse::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MovieResponse> {
                override fun onComplete() {
                    view.onSuccess(context, appName)
                }

                override fun onSubscribe(d: Disposable) {
                    compositeDisposable.add(d)
                }

                override fun onNext(t: MovieResponse) {
                    view.setAlarm(t.movieResult)
                }

                override fun onError(e: Throwable) {
                    view.handleError(e, context)
                }
            })
    }
}