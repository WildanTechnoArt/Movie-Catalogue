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
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.adapter.CastListAdapter
import com.wildan.moviecatalogue.adapter.CrewListAdapter
import com.wildan.moviecatalogue.adapter.RecommendTvAdapter
import com.wildan.moviecatalogue.adapter.TvShowAdapterListener
import com.wildan.moviecatalogue.database.AppDatabase
import com.wildan.moviecatalogue.database.TvShowEntity
import com.wildan.moviecatalogue.model.credit.CastData
import com.wildan.moviecatalogue.model.credit.CrewData
import com.wildan.moviecatalogue.model.favorite.Favorite
import com.wildan.moviecatalogue.model.tv.DetailTvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.presenter.DetailTvShowPresenter
import com.wildan.moviecatalogue.presenter.FavoritePresenter
import com.wildan.moviecatalogue.presenter.FavoriteTvPresenter
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import com.wildan.moviecatalogue.view.CreditView
import com.wildan.moviecatalogue.view.DetailTvShowView
import com.wildan.moviecatalogue.view.FavoriteView
import com.wildan.moviecatalogue.view.RecommendedView
import com.wildan.moviecatalogue.viewmodel.TvCreditViewModel
import com.wildan.moviecatalogue.widget.FavoriteWidget
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.credit_content.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.properties.Delegates

class DetailTvShowActivity : AppCompatActivity(), DetailTvShowView.View, CreditView.View, TvShowAdapterListener,
    RecommendedView.View{

    private lateinit var presenterTv: DetailTvShowView.Presenter
    private lateinit var favoriteMoviePresenter: FavoriteView.TvPresenter
    private lateinit var favoritePresenter: FavoriteView.FavoritePresenter
    private lateinit var mCreditViewModel: TvCreditViewModel
    private var castAdapter by Delegates.notNull<CastListAdapter>()
    private var crewAdapter by Delegates.notNull<CrewListAdapter>()
    private var recommendAdapter by Delegates.notNull<RecommendTvAdapter>()
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
    private var favorite = Favorite()
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        layout_data.visibility = View.VISIBLE

        tvTitle = savedInstanceState.getString(UtilsConstant.SAVE_TITLE)
        tvDate = savedInstanceState.getString(UtilsConstant.SAVE_DATE)
        tvRating = savedInstanceState.getString(UtilsConstant.SAVE_RATING)
        tvOverview = savedInstanceState.getString(UtilsConstant.SAVE_OVERVIEW)
        tvGenres = savedInstanceState.getString(UtilsConstant.SAVE_GENRES)
        tvPoster = savedInstanceState.getString(UtilsConstant.SAVE_POSTER)
        tvBackdrop = savedInstanceState.getString(UtilsConstant.SAVE_BACKDROP)

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

    override fun onTvClickListener(tvId: String) {
        val intent = Intent(this, DetailMovieActivity::class.java)
        intent.putExtra(MOVIE_EXTRA, tvId)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        AppDatabase.destroyInstance()
        favoriteMoviePresenter.onDestroy()
        favoritePresenter.onDestroy()
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

        presenterTv = DetailTvShowPresenter(this)

        requestData()

        prepareAdapter()

        rv_main_actor.setHasFixedSize(true)
        rv_main_actor.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
        rv_main_actor.adapter = castAdapter

        rv_crew.setHasFixedSize(true)
        rv_crew.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.HORIZONTAL, false)
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

        favoriteMoviePresenter = FavoriteTvPresenter(movieDatabase.tvShowDao())
        favoritePresenter = FavoritePresenter()

        favorite.id = mId.toInt()
    }

    private fun requestData(){
        mCreditViewModel = ViewModelProviders.of(this).get(TvCreditViewModel::class.java)
        mCreditViewModel.getCastList().observe(this, getCastList)
        mCreditViewModel.getCrewList().observe(this, getCrewList)
        mCreditViewModel.getListRecommend().observe(this, getListRecommend)
    }

    private fun prepareAdapter(){
        castAdapter = CastListAdapter()
        castAdapter.notifyDataSetChanged()
        crewAdapter = CrewListAdapter()
        crewAdapter.notifyDataSetChanged()
        recommendAdapter = RecommendTvAdapter(this)
        recommendAdapter.notifyDataSetChanged()
    }

    private fun loadDataMovie() {
        when (Locale.getDefault().language.toString()) {
            "en" -> myLanguage = "en"
            "in" -> myLanguage = "id"
        }
        presenterTv.getDetailTvShow(API_KEY, mId, myLanguage)
        mCreditViewModel.setCreditData(API_KEY, mId, this)
        mCreditViewModel.setRecommended(1, API_KEY, mId, this)
    }

    private fun addToFavorite() {
        // Menambahkan ke daftar tv show favorite
        tvEntity.tvId = mId
        tvEntity.tvName = tvTitle
        tvEntity.tvDate = tvDate
        tvEntity.tvGenre = tvGenres
        tvEntity.tvOverview = tvOverview
        tvEntity.tvPoster = tvPoster
        tvEntity.tvRating = tvRating
        tvEntity.tvBackdrop = tvBackdrop
        tvEntity.tvPopularity = tvPopularity

        favoriteMoviePresenter.insertTvShow(tvEntity)

        favorite.title = tvTitle
        favorite.releaseDate = tvDate
        favorite.posterPath = tvPoster
        favorite.popularity = tvPopularity
        favorite.voteAverage = tvRating

        favoritePresenter.insertFavorite(this, favorite)

        sendUpdateFavoriteList(this)

        Toast.makeText(this, resources.getString(R.string.add_favorite), Toast.LENGTH_SHORT).show()
    }

    private fun removeFromFavorite() {
        favoriteMoviePresenter.deleteTvShow(mId)
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

    private fun sendUpdateFavoriteList(context: Context) {
        val i = Intent(context, FavoriteWidget::class.java)
        i.action = FavoriteWidget.UPDATE_WIDGET
        context.sendBroadcast(i)
    }

    private val getCastList = Observer<ArrayList<CastData>> { castItems ->
        if(castItems != null){
            castAdapter.setData(castItems)
        }
    }

    private val getCrewList = Observer<ArrayList<CrewData>> { crewItems ->
        if(crewItems != null){
            crewAdapter.setData(crewItems)
        }
    }

    private val getListRecommend = Observer<ArrayList<TvShowResult>> { tvItems ->
        if (tvItems != null) {
            recommendAdapter.setData(tvItems)
        }
    }
}
