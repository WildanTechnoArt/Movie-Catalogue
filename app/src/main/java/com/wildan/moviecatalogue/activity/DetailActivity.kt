package com.wildan.moviecatalogue.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast

import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.network.BaseApiService
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkClient
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.DetailMoviePresenter
import com.wildan.moviecatalogue.presenter.DetailTvShowPresenter
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.DETAIL_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import com.wildan.moviecatalogue.view.DetailMovieView
import com.wildan.moviecatalogue.view.DetailTvShowView

import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class DetailActivity : AppCompatActivity(), DetailMovieView.View, DetailTvShowView.View {

    private lateinit var presenterMovie: DetailMovieView.Presenter
    private lateinit var presenterTv: DetailTvShowView.Presenter
    private var baseApiService: BaseApiService? = null
    private lateinit var movieType: String
    private lateinit var mId: String
    private var genres: String = ""
    private lateinit var myLanguage: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
        loadDataMovie()

        swipe_refresh.setOnRefreshListener {
            loadDataMovie()
        }
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        baseApiService = NetworkClient.getClient(this)
            ?.create(BaseApiService::class.java)

        val repository = baseApiService?.let { MovieRepositoryImp(it) }

        presenterMovie = DetailMoviePresenter(this, repository)
        presenterTv = DetailTvShowPresenter(this, repository)

        mId = intent.getStringExtra(MOVIE_EXTRA)
        movieType = intent.getStringExtra(DETAIL_EXTRA)
    }

    private fun loadDataMovie() {
        when (Locale.getDefault().language.toString()) {
            "en" -> myLanguage = "en"
            "in" -> myLanguage = "id"
        }

        when (movieType) {
            "movie" -> presenterMovie.getDetailMovie(mId, API_KEY, myLanguage)
            "tv" -> presenterTv.getDetailTvShow(mId, API_KEY, myLanguage)
        }
    }

    private fun getDetailMovie(
        posterPath: String,
        title: String,
        vote: String,
        date: String,
        overview: String,
        backdropPath: String
    ) {
        GlideApp.with(this)
            .load(POSTER_URL + posterPath)
            .placeholder(R.drawable.ic_image_placeholder_32dp)
            .error(R.drawable.ic_error_image_32dp)
            .into(img_poster)

        GlideApp.with(this)
            .load(BACKDROP_URL + backdropPath)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_white_32dp)
            .error(R.drawable.ic_error_image_white_32dp)
            .into(img_backdrop)

        tv_title.text = title
        tv_rating.text = String.format(resources.getString(R.string.movie_rating), vote)
        tv_date.text = DateFormat.getLongDate(date)
        tv_description.text = overview
    }

    override fun showDetailMovie(movie: DetailMovieResponse) {
        getDetailMovie(
            movie.posterPath.toString(),
            movie.title.toString(),
            movie.voteAverage.toString(),
            movie.releaseDate.toString(),
            movie.overview.toString(),
            movie.backdropPath.toString()
        )

        genres = ""
        for (genre in movie.genreList) {
            genres += genre.name.toString() + ", "
        }
        val genreResult = genres.substring(0, genres.length.minus(2))
        tv_genres.text = genreResult

        if (movie.runtime != null) {
            tv_duration.text = String.format(resources.getString(R.string.movie_duration), movie.runtime.toString())
        } else {
            tv_duration.visibility = View.GONE
            txt_duration.visibility = View.GONE
        }
        tv_genres.isSelected = true
    }

    override fun noInternetConnection(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showProgressBar() {
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        swipe_refresh.isRefreshing = false
    }

    override fun onSuccess() {
        swipe_refresh.isRefreshing = false
        layout_data.visibility = View.VISIBLE
    }

    override fun showDetailTvShow(tv: DetailTvShowResponse) {
        getDetailMovie(
            tv.posterPath.toString(),
            tv.name.toString(),
            tv.voteAverage.toString(),
            tv.firstAirDate.toString(),
            tv.overview.toString(),
            tv.backdropPath.toString()
        )

        genres = ""
        for (genre in tv.genreList) {
            genres += genre.name.toString() + ", "
        }
        val genreResult = genres.substring(0, genres.length.minus(2))
        tv_genres.text = genreResult
        tv_genres.isSelected = true

        tv_duration.visibility = View.GONE
        txt_duration.visibility = View.GONE
    }

    override fun handleError(t: Throwable?) {
        if (ConnectivityStatus.isConnected(this)) {
            when (t) {
                is HttpException -> // non 200 error codes
                    NetworkError.handleError(t, this)
                is SocketTimeoutException -> // connection errors
                    Toast.makeText(this, resources.getString(R.string.timeout), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
