package com.wildan.moviecatalogue.repository

import com.wildan.moviecatalogue.model.movie.*
import com.wildan.moviecatalogue.model.tv.*
import com.wildan.moviecatalogue.network.BaseApiService
import io.reactivex.Flowable

class MovieRepositoryImp(private val baseApiService: BaseApiService) : MovieRepository {

    override fun getMovieData(apiKey: String, page: Int, language: String): Flowable<MovieResponse> =
        baseApiService.getUpcomingMovie(apiKey, page, language)

    override fun getOnTheAirMovie(apiKey: String, page: Int, language: String): Flowable<TvShowResponse> =
        baseApiService.getOnTheAirMovie(apiKey, page, language)

    override fun getDetailMovie(movieId: String, apiKey: String, language: String): Flowable<DetailMovieResponse> =
        baseApiService.getDetailMovie(movieId, apiKey, language)

    override fun getDetailTvShow(tvId: String, apiKey: String, language: String): Flowable<DetailTvShowResponse> =
        baseApiService.getDetailTvShow(tvId, apiKey, language)
}