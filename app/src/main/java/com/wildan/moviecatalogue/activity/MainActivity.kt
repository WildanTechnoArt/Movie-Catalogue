package com.wildan.moviecatalogue.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.adapter.ListMovieAdapter
import com.wildan.moviecatalogue.adapter.ListTvAdapter
import com.wildan.moviecatalogue.adapter.MovieAdapterListener
import com.wildan.moviecatalogue.adapter.TvShowAdapterListener
import com.wildan.moviecatalogue.fragment.FavoriteFragment
import com.wildan.moviecatalogue.fragment.MovieFragment
import com.wildan.moviecatalogue.fragment.TvShowFragment
import com.wildan.moviecatalogue.model.movie.MovieResponse
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MENU_CONDITION
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SAVE_VIEW
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.SEARCH_QUERY
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.STATE_SAVED
import com.wildan.moviecatalogue.view.SearchListView
import com.wildan.moviecatalogue.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*

class MainActivity : AppCompatActivity(), SearchListView.View,
    BottomNavigationView.OnNavigationItemSelectedListener, MovieAdapterListener, TvShowAdapterListener {

    private lateinit var searchViewModel: SearchViewModel
    private var searchMenuItem: MenuItem? = null
    private var mQuery: String? = null
    private var movieAdapter = ListMovieAdapter(null)
    private var tvAdapter = ListTvAdapter(null)
    private var page: Int = 1
    private var totalPage: Int? = null
    private var isLoading = false
    private var lastLocale: String? = null
    private var tvShowSelected = false
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepare()
        scrollListener()

        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.movie_menu
        } else {
            if (savedInstanceState.getInt(SAVE_VIEW) == View.VISIBLE) {
                tvShowSelected = savedInstanceState.getBoolean(MENU_CONDITION)
                if (tvShowSelected) {
                    tvShowPrepare()
                } else {
                    moviePrepare()
                }
                swipe_refresh.visibility = View.VISIBLE
                bottom_navigation.visibility = View.GONE
                content_view.visibility = View.GONE
                mQuery = savedInstanceState.getString(SEARCH_QUERY).toString()
            }
        }

        swipe_refresh.setOnRefreshListener {
            if (tvShowSelected) {
                showListTv(mQuery)
            } else {
                showListMovie(mQuery)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)

        searchMenuItem = menu?.findItem(R.id.search)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView = searchMenuItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.search)

        if (swipe_refresh.visibility == View.VISIBLE) {
            searchMenuItem?.expandActionView()
            searchView.setQuery(mQuery, true)
            searchView.clearFocus()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            private var timer = Timer()
            private val DELAY: Long = 1000

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    mQuery = newText
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                runOnUiThread {
                                    if (tvShowSelected) {
                                        tvAdapter.clearMovie()
                                        showListTv(mQuery)
                                    } else {
                                        movieAdapter.clearMovie()
                                        showListMovie(mQuery)
                                    }
                                }
                            }
                        },
                        DELAY
                    )
                }
                return true
            }
        })

        searchMenuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                bottom_navigation.visibility = View.GONE
                content_view.visibility = View.GONE
                swipe_refresh.visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                if (tvShowSelected) {
                    tvAdapter.clearMovie()
                } else {
                    movieAdapter.clearMovie()
                }
                swipe_refresh.visibility = View.GONE
                searchView.onActionViewCollapsed()
                bottom_navigation.visibility = View.VISIBLE
                content_view.visibility = View.VISIBLE
                return true
            }
        })

        return true
    }

    override fun getTvShowData(tv: TvShowResponse) {
        totalPage = tv.totalPages
    }

    override fun getMovieData(movie: MovieResponse) {
        totalPage = movie.totalPages
    }

    override fun showProgressBar() {
        isLoading = true
        swipe_refresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        isLoading = false
        swipe_refresh.isRefreshing = false
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_SAVED, "saved")
        outState.putInt(SAVE_VIEW, swipe_refresh.visibility)
        outState.putBoolean(MENU_CONDITION, tvShowSelected)
        outState.putString(SEARCH_QUERY, searchView.query.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.setting -> startActivity(Intent(this, SettingsActivity::class.java))
        }
        return true
    }

    override fun onNavigationItemSelected(menu: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (menu.itemId) {
            R.id.movie_menu -> {
                fragment = MovieFragment()
                moviePrepare()
                tvShowSelected = false
                searchMenuItem?.isVisible = true
            }
            R.id.tvshow_menu -> {
                fragment = TvShowFragment()
                tvShowPrepare()
                tvShowSelected = true
                searchMenuItem?.isVisible = true
            }
            R.id.favorite_menu -> {
                fragment = FavoriteFragment()
                searchMenuItem?.isVisible = false
            }
        }
        return loadFragment(fragment)
    }

    override fun onMovieClickListener(movieId: String) {
        val intent = Intent(this, DetailMovieActivity::class.java)
        intent.putExtra(MOVIE_EXTRA, movieId)
        startActivity(intent)
    }

    override fun onTvClickListener(tvId: String) {
        val intent = Intent(this, DetailTvShowActivity::class.java)
        intent.putExtra(MOVIE_EXTRA, tvId)
        startActivity(intent)
    }

    private fun prepare() {
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        AndroidNetworking.initialize(this)

        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        rv_movie_list.setHasFixedSize(true)
        rv_movie_list.layoutManager = LinearLayoutManager(this)
    }

    private fun tvShowPrepare() {
        tvAdapter = ListTvAdapter(this)
        searchViewModel.getTvShows().observe(this, getTvShow)
        tvAdapter.notifyDataSetChanged()
        rv_movie_list.adapter = tvAdapter
    }

    private fun moviePrepare() {
        movieAdapter = ListMovieAdapter(this)
        searchViewModel.getMovies().observe(this, getMovie)
        movieAdapter.notifyDataSetChanged()
        rv_movie_list.adapter = movieAdapter
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_view, fragment)
                .commit()
            return true
        }
        return false
    }

    private fun showListMovie(query: String?) {
        lastLocale = Locale.getDefault().language.toString()
        when (Locale.getDefault().language.toString()) {
            "en" -> searchViewModel.searchMovie(API_KEY, query, page = page, language = "en", view = this)
            "in" -> searchViewModel.searchMovie(API_KEY, query, page = page, language = "id", view = this)
        }
    }

    private fun showListTv(query: String?) {
        lastLocale = Locale.getDefault().language.toString()
        when (Locale.getDefault().language.toString()) {
            "en" -> searchViewModel.searchTvShow(API_KEY, query, page = page, language = "en", view = this)
            "in" -> searchViewModel.searchTvShow(API_KEY, query, page = page, language = "id", view = this)
        }
    }

    private fun scrollListener() {
        rv_movie_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!isLoading && isLastPosition && page != totalPage) {
                    page = page.plus(1)
                    showListMovie(mQuery)
                }
            }
        })
    }

    private val getMovie = Observer<ArrayList<MovieResult>> { movieItems ->
        if (movieItems != null) {
            if (page == 1) {
                movieAdapter.setData(movieItems)
            } else {
                movieAdapter.refreshAdapter(movieItems)
            }

            if (movieAdapter.itemCount > 0) {
                rv_movie_list.visibility = View.VISIBLE
                tv_movie_not_found.visibility = View.GONE
            } else {
                rv_movie_list.visibility = View.GONE
                tv_movie_not_found.visibility = View.VISIBLE
            }
        }
    }

    private val getTvShow = Observer<ArrayList<TvShowResult>> { tvItems ->
        if (tvItems != null) {
            if (page == 1) {
                tvAdapter.setData(tvItems)
            } else {
                tvAdapter.refreshAdapter(tvItems)
            }

            if (tvAdapter.itemCount > 0) {
                rv_movie_list.visibility = View.VISIBLE
                tv_movie_not_found.visibility = View.GONE
            } else {
                rv_movie_list.visibility = View.GONE
                tv_movie_not_found.visibility = View.VISIBLE
            }
        }
    }
}
