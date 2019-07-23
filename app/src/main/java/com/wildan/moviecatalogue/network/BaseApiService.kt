package com.wildan.moviecatalogue.network

import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResponse

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BaseApiService {

    @GET("discover/movie")
    fun getUpcomingMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ): Flowable<MovieResponse>

    @GET("movie/{movie_id}")
    fun getDetailMovie(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Flowable<DetailMovieResponse>

    @GET("discover/tv")
    fun getOnTheAirMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ): Flowable<TvShowResponse>

    @GET("tv/{tv_id}")
    fun getDetailTvShow(
        @Path("tv_id") tvId: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Flowable<DetailTvShowResponse>
}