package com.wildan.moviecatalogue.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.database.AppDatabase
import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.network.BaseApiService
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkClient
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.DetailMoviePresenter
import com.wildan.moviecatalogue.presenter.FavoriteMoviePresenter
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_BACKDROP
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_DATE
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_DURATION
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_GENRES
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_OVERVIEW
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_POSTER
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_RATING
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_TITLE
import com.wildan.moviecatalogue.view.DetailMovieView
import com.wildan.moviecatalogue.view.FavoriteView
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class DetailMovieActivity : AppCompatActivity(), DetailMovieView.View {

    private lateinit var presenterMovie: DetailMovieView.Presenter
    private lateinit var favoritePresenter: FavoriteView.MoviePresenter
    private var baseApiService: BaseApiService? = null
    private lateinit var mId: String
    private var genres: String = ""
    private lateinit var myLanguage: String
    private lateinit var movieDatabase: AppDatabase

    private var movieTitle: String? = null
    private var movieDate: String? = null
    private var movieRating: String? = null
    private var moviePoster: String? = null
    private var movieBackdrop: String? = null
    private var movieDuration: String? = null
    private var movieOverview: String? = null
    private var moviePopularity: String? = null
    private val movieEntity = MovieEntity()
    private var menuItem: Menu? = null
    private var movieGenres: String? = null
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
        favoriteState()

        if (savedInstanceState == null) {
            loadDataMovie()
        }

        swipe_refresh.setOnRefreshListener {
            loadDataMovie()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        layout_data.visibility = View.VISIBLE

        movieTitle = savedInstanceState?.getString(SAVE_TITLE)
        movieDate = savedInstanceState?.getString(SAVE_DATE)
        movieRating = savedInstanceState?.getString(SAVE_RATING)
        movieOverview = savedInstanceState?.getString(SAVE_OVERVIEW)
        movieGenres = savedInstanceState?.getString(SAVE_GENRES)
        moviePoster = savedInstanceState?.getString(SAVE_POSTER)
        movieBackdrop = savedInstanceState?.getString(SAVE_BACKDROP)
        movieDuration = savedInstanceState?.getString(SAVE_DURATION)

        getDetailMovie()
    }

    private fun init() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        baseApiService = NetworkClient.getClient(this)
            ?.create(BaseApiService::class.java)

        movieDatabase = AppDatabase.getInstance(this)

        val repository = baseApiService?.let { MovieRepositoryImp(it) }

        presenterMovie = DetailMoviePresenter(this, repository)
        favoritePresenter = FavoriteMoviePresenter(movieDatabase.movieDao())

        mId = intent.getStringExtra(MOVIE_EXTRA)
    }

    private fun loadDataMovie() {
        when (Locale.getDefault().language.toString()) {
            "en" -> myLanguage = "en"
            "in" -> myLanguage = "id"
        }
        presenterMovie.getDetailMovie(mId, API_KEY, myLanguage)
    }

    private fun addToFavorite() {
        movieEntity.movieId = mId
        movieEntity.movieName = movieTitle
        movieEntity.movieDate = movieDate
        movieEntity.movieGenre = movieGenres
        movieEntity.movieOverview = movieOverview
        movieEntity.moviePoster = moviePoster
        movieEntity.movieRating = movieRating
        movieEntity.movieBackdrop = movieBackdrop
        movieEntity.moviePopularity = moviePopularity

        favoritePresenter.insertMovie(movieEntity)

        Toast.makeText(this, resources.getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun removeFromFavorite() {
        favoritePresenter.deleteMovie(mId)
        Toast.makeText(this, getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show()

    }

    private fun setFavorite() {
        if (isFavorite)
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_white_24dp)
        else
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_24dp)
    }

    private fun favoriteState() {
        val favorite = movieDatabase.movieDao().getMovieById(mId)
        if (favorite.isNotEmpty()) isFavorite = true
    }

    private fun getDetailMovie() {

        GlideApp.with(this)
            .load(POSTER_URL + moviePoster)
            .placeholder(R.drawable.ic_image_placeholder_32dp)
            .error(R.drawable.ic_error_image_32dp)
            .into(img_poster)

        GlideApp.with(this)
            .load(BACKDROP_URL + movieBackdrop)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_white_32dp)
            .error(R.drawable.ic_error_image_white_32dp)
            .into(img_backdrop)

        tv_title.text = movieTitle
        tv_rating.text = String.format(resources.getString(R.string.movie_rating), movieRating)
        tv_date.text = movieDate?.let { DateFormat.getLongDate(it) }
        tv_description.text = movieOverview
        tv_genres.text = movieGenres
        tv_genres.isSelected = true
        tv_duration.text = movieDuration
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_detail, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            R.id.add_favorite -> {
                if (isFavorite) removeFromFavorite() else addToFavorite()

                isFavorite = !isFavorite
                setFavorite()
                true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun showDetailMovie(movie: DetailMovieResponse) {
        movieTitle = movie.title.toString()
        movieDate = movie.releaseDate.toString()
        movieRating = movie.voteAverage.toString()
        moviePoster = movie.posterPath.toString()
        movieBackdrop = movie.backdropPath.toString()
        movieOverview = movie.overview.toString()
        moviePopularity = movie.popularity.toString()

        genres = ""
        for (genre in movie.genreList) {
            genres += genre.name.toString() + ", "
        }

        val genreResult = genres.substring(0, genres.length.minus(2))
        movieGenres = genreResult

        if (movie.runtime != null) {
            tv_duration.visibility = View.VISIBLE
            txt_duration.visibility = View.VISIBLE
            movieDuration = String.format(resources.getString(R.string.movie_duration), movie.runtime.toString())
        }

        getDetailMovie()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_TITLE, movieTitle)
        outState.putString(SAVE_DATE, movieDate)
        outState.putString(SAVE_RATING, movieRating)
        outState.putString(SAVE_OVERVIEW, movieOverview)
        outState.putString(SAVE_GENRES, movieGenres)
        outState.putString(SAVE_POSTER, moviePoster)
        outState.putString(SAVE_BACKDROP, movieBackdrop)
        outState.putString(SAVE_DURATION, movieDuration)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
        favoritePresenter.onDestroy()
    }
}