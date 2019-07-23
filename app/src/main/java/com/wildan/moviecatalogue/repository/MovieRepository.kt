package com.wildan.moviecatalogue.repository

import com.wildan.moviecatalogue.model.movie.*
import com.wildan.moviecatalogue.model.tv.*
import io.reactivex.Flowable

interface MovieRepository {
    fun getMovieData(apiKey: String, page: Int, language: String): Flowable<MovieResponse>
    fun getOnTheAirMovie(apiKey: String, page: Int, language: String): Flowable<TvShowResponse>
    fun getDetailMovie(movieId: String, apiKey: String, language: String): Flowable<DetailMovieResponse>
    fun getDetailTvShow(tvId: String, apiKey: String, language: String): Flowable<DetailTvShowResponse>
}