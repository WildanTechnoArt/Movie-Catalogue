package com.wildan.moviecatalogue.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.database.AppDatabase
import com.wildan.moviecatalogue.database.TvShowEntity
import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.network.BaseApiService
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkClient
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.DetailTvShowPresenter
import com.wildan.moviecatalogue.presenter.FavoriteTvPresenter
import com.wildan.moviecatalogue.repository.MovieRepositoryImp
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import com.wildan.moviecatalogue.view.DetailTvShowView
import com.wildan.moviecatalogue.view.FavoriteView
import kotlinx.android.synthetic.main.activity_detail.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class DetailTvShowActivity : AppCompatActivity(), DetailTvShowView.View {

    private lateinit var presenterTv: DetailTvShowView.Presenter
    private lateinit var favoritePresenter: FavoriteView.TvPresenter
    private var baseApiService: BaseApiService? = null
    private lateinit var mId: String
    private var genres: String = ""
    private lateinit var myLanguage: String
    private lateinit var movieDatabase: AppDatabase

    private var tvTitle: String? = null
    private var tvDate: String? = null
    private var tvRating: String? = null
    private var tvPoster: String? = null
    private var tvBackdrop: String? = null
    private var tvOverview: String? = null
    private var tvPopularity: String? = null
    private val tvEntity = TvShowEntity()
    private var menuItem: Menu? = null
    private var tvGenres: String? = null
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

        tvTitle = savedInstanceState?.getString(UtilsConstant.SAVE_TITLE)
        tvDate = savedInstanceState?.getString(UtilsConstant.SAVE_DATE)
        tvRating = savedInstanceState?.getString(UtilsConstant.SAVE_RATING)
        tvOverview = savedInstanceState?.getString(UtilsConstant.SAVE_OVERVIEW)
        tvGenres = savedInstanceState?.getString(UtilsConstant.SAVE_GENRES)
        tvPoster = savedInstanceState?.getString(UtilsConstant.SAVE_POSTER)
        tvBackdrop = savedInstanceState?.getString(UtilsConstant.SAVE_BACKDROP)

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

        presenterTv = DetailTvShowPresenter(this, repository)
        favoritePresenter = FavoriteTvPresenter(movieDatabase.tvShowDao())

        mId = intent.getStringExtra(MOVIE_EXTRA)
    }

    private fun loadDataMovie() {
        when (Locale.getDefault().language.toString()) {
            "en" -> myLanguage = "en"
            "in" -> myLanguage = "id"
        }
        presenterTv.getDetailTvShow(mId, API_KEY, myLanguage)
    }

    private fun addToFavorite() {
        tvEntity.tvId = mId
        tvEntity.tvName = tvTitle
        tvEntity.tvDate = tvDate
        tvEntity.tvGenre = tvGenres
        tvEntity.tvOverview = tvOverview
        tvEntity.tvPoster = tvPoster
        tvEntity.tvRating = tvRating
        tvEntity.tvBackdrop = tvBackdrop
        tvEntity.tvPopularity = tvPopularity

        favoritePresenter.insertTvShow(tvEntity)

        Toast.makeText(this, resources.getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun removeFromFavorite() {
        favoritePresenter.deleteTvShow(mId)
        Toast.makeText(this, getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show()

    }

    private fun setFavorite() {
        if (isFavorite)
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_white_24dp)
        else
            menuItem?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_star_border_white_24dp)
    }

    private fun favoriteState() {
        val favorite = movieDatabase.tvShowDao().getTvShowById(mId)
        if (favorite.isNotEmpty()) isFavorite = true
    }

    private fun getDetailMovie() {

        GlideApp.with(this)
            .load(POSTER_URL + tvPoster)
            .placeholder(R.drawable.ic_image_placeholder_32dp)
            .error(R.drawable.ic_error_image_32dp)
            .into(img_poster)

        GlideApp.with(this)
            .load(BACKDROP_URL + tvBackdrop)
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder_white_32dp)
            .error(R.drawable.ic_error_image_white_32dp)
            .into(img_backdrop)

        tv_title.text = tvTitle
        tv_rating.text = String.format(resources.getString(R.string.movie_rating), tvRating)
        tv_date.text = tvDate?.let { DateFormat.getLongDate(it) }
        tv_description.text = tvOverview
        tv_genres.text = tvGenres
        tv_genres.isSelected = true
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

        tvTitle = tv.name.toString()
        tvDate = tv.firstAirDate.toString()
        tvRating = tv.voteAverage.toString()
        tvPoster = tv.posterPath.toString()
        tvBackdrop = tv.backdropPath.toString()
        tvOverview = tv.overview.toString()
        tvPopularity = tv.popularity.toString()

        genres = ""
        for (genre in tv.genreList) {
            genres += genre.name.toString() + ", "
        }

        val genreResult = genres.substring(0, genres.length.minus(2))
        tvGenres = genreResult

        getDetailMovie()
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
        outState.putString(UtilsConstant.SAVE_TITLE, tvTitle)
        outState.putString(UtilsConstant.SAVE_DATE, tvDate)
        outState.putString(UtilsConstant.SAVE_RATING, tvRating)
        outState.putString(UtilsConstant.SAVE_OVERVIEW, tvOverview)
        outState.putString(UtilsConstant.SAVE_GENRES, tvGenres)
        outState.putString(UtilsConstant.SAVE_POSTER, tvPoster)
        outState.putString(UtilsConstant.SAVE_BACKDROP, tvBackdrop)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
        favoritePresenter.onDestroy()
    }
}
