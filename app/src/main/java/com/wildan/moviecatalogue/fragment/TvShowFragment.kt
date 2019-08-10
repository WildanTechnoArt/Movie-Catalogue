package com.wildan.moviecatalogue.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.activity.DetailTvShowActivity
import com.wildan.moviecatalogue.adapter.ListTvAdapter
import com.wildan.moviecatalogue.adapter.TvShowAdapterListener
import com.wildan.moviecatalogue.model.tv.TvShowResponse
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.network.ConnectivityStatus
import com.wildan.moviecatalogue.network.NetworkError
import com.wildan.moviecatalogue.utils.UtilsConstant
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.API_KEY
import com.wildan.moviecatalogue.view.TvShowView
import com.wildan.moviecatalogue.viewmodel.TvViewModel
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class TvShowFragment : Fragment(), TvShowView.View, TvShowAdapterListener {

    private lateinit var rvTvShow: RecyclerView

    private lateinit var tvViewModel: TvViewModel
    private var adapter by Delegates.notNull<ListTvAdapter>()
    private var page: Int = 1
    private var totalPage: Int? = null
    private var isLoading by Delegates.notNull<Boolean>()
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var lastLocale: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvTvShow = view.findViewById(R.id.rv_movie)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)

        prepare()
        scrollListener()

        if (savedInstanceState == null) {
            showListTvShow()
        }

        swipeRefresh.setOnRefreshListener {
            showListTvShow()
        }

    }

    override fun onResume() {
        super.onResume()
        if (lastLocale != Locale.getDefault().language.toString()) {
            showListTvShow()
        }
    }

    override fun getTvShowData(tv: TvShowResponse) {
        totalPage = tv.totalPages
    }

    override fun showProgressBar() {
        isLoading = true
        swipeRefresh.isRefreshing = true
    }

    override fun hideProgressBar() {
        isLoading = false
        swipeRefresh.isRefreshing = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(UtilsConstant.STATE_SAVED, "saved")
    }

    override fun handleError(t: Throwable?) {
        if (ConnectivityStatus.isConnected(context)) {
            when (t) {
                is HttpException -> // non 200 error codes
                    NetworkError.handleError(t, context)
                is SocketTimeoutException -> // connection errors
                    Toast.makeText(context, resources.getString(R.string.timeout), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, resources.getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onTvClickListener(tvId: String) {
        val intent = Intent(context, DetailTvShowActivity::class.java)
        intent.putExtra(UtilsConstant.MOVIE_EXTRA, tvId)
        startActivity(intent)
    }

    private fun prepare() {
        AndroidNetworking.initialize(context)

        tvViewModel = ViewModelProviders.of(this).get(TvViewModel::class.java)
        tvViewModel.getTvShows().observe(this, getTvShow)

        adapter = ListTvAdapter(this)
        adapter.notifyDataSetChanged()

        rvTvShow.setHasFixedSize(true)
        rvTvShow.layoutManager = LinearLayoutManager(context)

        rvTvShow.adapter = adapter
    }

    private fun scrollListener() {
        rvTvShow.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
                val countItem = linearLayoutManager.itemCount
                val lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                val isLastPosition = countItem.minus(1) == lastVisiblePosition
                if (!isLoading && isLastPosition && page < totalPage ?: 0) {
                    page = page.plus(1)
                    showListTvShow()
                }
            }
        })
    }

    private fun showListTvShow() {
        lastLocale = Locale.getDefault().language.toString()
        when (Locale.getDefault().language.toString()) {
            "en" -> tvViewModel.setTvShow(API_KEY, page = page, language = "en", view = this)
            "in" -> tvViewModel.setTvShow(API_KEY, page = page, language = "id", view = this)
        }
    }

    private val getTvShow = Observer<ArrayList<TvShowResult>> { movieItems ->
        if (movieItems != null) {
            if (page == 1) {
                adapter.setData(movieItems)
            } else {
                adapter.refreshAdapter(movieItems)
            }
        }
    }
}
