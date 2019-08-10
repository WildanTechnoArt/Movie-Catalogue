package com.wildan.moviecatalogue.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.adapter.CastListAdapter
import com.wildan.moviecatalogue.adapter.CrewListAdapter
import com.wildan.moviecatalogue.adapter.MovieAdapterListener
import com.wildan.moviecatalogue.adapter.RecommendMovieAdapter
import com.wildan.moviecatalogue.database.AppDatabase
import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.model.credit.CastData
import com.wildan.moviecatalogue.model.credit.CrewData
import com.wildan.moviecatalogue.model.favorite.Favorite
import com.wildan.moviecatalogue.model.movie.DetailMovieResponse
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.DetailMoviePresenter
import com.wildan.moviecatalogue.presenter.FavoriteMoviePresenter
import com.wildan.moviecatalogue.presenter.FavoritePresenter
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
import com.wildan.moviecatalogue.view.CreditView
import com.wildan.moviecatalogue.view.DetailMovieView
import com.wildan.moviecatalogue.view.FavoriteView
import com.wildan.moviecatalogue.view.RecommendedView
import com.wildan.moviecatalogue.viewmodel.MovieCreditViewModel
import com.wildan.moviecatalogue.widget.FavoriteWidget
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.credit_content.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.properties.Delegates

class DetailMovieActivity : AppCompatActivity(), DetailMovieView.View, CreditView.View, MovieAdapterListener,
    RecommendedView.View {

    private lateinit var presenterMovie: DetailMovieView.Presenter
    private lateinit var favoriteMoviePresenter: FavoriteView.MoviePresenter
    private lateinit var favoritePresenter: FavoriteView.FavoritePresenter
    private lateinit var mCreditViewModel: MovieCreditViewModel
    private var castAdapter by Delegates.notNull<CastListAdapter>()
    private var crewAdapter by Delegates.notNull<CrewListAdapter>()
    private var recommendAdapter by Delegates.notNull<RecommendMovieAdapter>()
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
    private var favorite = Favorite()
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        layout_data.visibility = View.VISIBLE

        movieTitle = savedInstanceState.getString(SAVE_TITLE)
        movieDate = savedInstanceState.getString(SAVE_DATE)
        movieRating = savedInstanceState.getString(SAVE_RATING)
        movieOverview = savedInstanceState.getString(SAVE_OVERVIEW)
        movieGenres = savedInstanceState.getString(SAVE_GENRES)
        moviePoster = savedInstanceState.getString(SAVE_POSTER)
        movieBackdrop = savedInstanceState.getString(SAVE_BACKDROP)
        movieDuration = savedInstanceState.getString(SAVE_DURATION)

        getDetailMovie()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_detail, menu)
        menuItem = menu
        setFavorite()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

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
        try {
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
        } catch (ex: Exception) {
            layout_data.visibility = View.INVISIBLE
            Toast.makeText(this, getString(R.string.error_detail_movie), Toast.LENGTH_SHORT).show()
        }
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

    override fun onMovieClickListener(movieId: String) {
        val intent = Intent(this, DetailMovieActivity::class.java)
        intent.putExtra(MOVIE_EXTRA, movieId)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
        favoriteMoviePresenter.onDestroy()
    }

    private fun init() {

        AndroidNetworking.initialize(applicationContext)

        mId = intent?.getStringExtra(MOVIE_EXTRA).toString()

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        movieDatabase = AppDatabase.getInstance(this)

        presenterMovie = DetailMoviePresenter(this)

        requestData()

        prepareAdapter()

        rv_main_actor.setHasFixedSize(true)
        rv_main_actor.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        rv_main_actor.adapter = castAdapter

        rv_crew.setHasFixedSize(true)
        rv_crew.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        rv_crew.adapter = crewAdapter

        rv_recommended.setHasFixedSize(true)
        rv_recommended.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL, false
        )
        rv_recommended.adapter = recommendAdapter

        val uri = intent.data
        if (uri != null) {
            val cursor = contentResolver.query(uri, null, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) favorite = Favorite(cursor)
                cursor.close()
            }
        }

        favoriteMoviePresenter = FavoriteMoviePresenter(movieDatabase.movieDao())
        favoritePresenter = FavoritePresenter()

        favorite.id = mId.toInt()
    }

    private fun prepareAdapter(){
        castAdapter = CastListAdapter()
        castAdapter.notifyDataSetChanged()
        crewAdapter = CrewListAdapter()
        crewAdapter.notifyDataSetChanged()
        recommendAdapter = RecommendMovieAdapter(this)
        recommendAdapter.notifyDataSetChanged()
    }

    private fun requestData(){
        mCreditViewModel = ViewModelProviders.of(this).get(MovieCreditViewModel::class.java)
        mCreditViewModel.getCastList().observe(this, getCastList)
        mCreditViewModel.getCrewList().observe(this, getCrewList)
        mCreditViewModel.getListRecommend().observe(this, getListRecommend)
    }

    private fun loadDataMovie() {
        when (Locale.getDefault().language.toString()) {
            "en" -> myLanguage = "en"
            "in" -> myLanguage = "id"
        }
        presenterMovie.getDetailMovie(API_KEY, mId, myLanguage)
        mCreditViewModel.setCreditData(API_KEY, mId, this)
        mCreditViewModel.setRecommended(1, API_KEY, mId, this)
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

        favoriteMoviePresenter.insertMovie(movieEntity)

        favorite.title = movieTitle
        favorite.releaseDate = movieDate
        favorite.posterPath = moviePoster
        favorite.popularity = moviePopularity
        favorite.voteAverage = movieRating

        favoritePresenter.insertFavorite(this, favorite)

        sendUpdateFavoriteList(this)

        Toast.makeText(this, resources.getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun removeFromFavorite() {
        favoriteMoviePresenter.deleteMovie(mId)
        favoritePresenter.deleteFavorite(this, favorite)
        sendUpdateFavoriteList(this)
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

    private fun sendUpdateFavoriteList(context: Context) {
        val i = Intent(context, FavoriteWidget::class.java)
        i.action = FavoriteWidget.UPDATE_WIDGET
        context.sendBroadcast(i)
    }

    private val getCastList = Observer<ArrayList<CastData>> { castItems ->
        if (castItems != null) {
            castAdapter.setData(castItems)
        }
    }

    private val getCrewList = Observer<ArrayList<CrewData>> { crewItems ->
        if (crewItems != null) {
            crewAdapter.setData(crewItems)
        }
    }

    private val getListRecommend = Observer<ArrayList<MovieResult>> { movieItems ->
        if (movieItems != null) {
            recommendAdapter.setData(movieItems)
        }
    }
}