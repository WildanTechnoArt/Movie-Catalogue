package com.wildan.moviecatalogue.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.activity.DetailMovieActivity
import com.wildan.moviecatalogue.adapter.MovieFavoriteAdapter
import com.wildan.moviecatalogue.adapter.MovieAdapterListener
import com.wildan.moviecatalogue.database.AppDatabase
import com.wildan.moviecatalogue.database.MovieEntity
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.MOVIE_EXTRA
import kotlin.properties.Delegates

class MovieFavoriteFragment : Fragment(), MovieAdapterListener {

    private lateinit var rvMovie: RecyclerView
    private lateinit var tvNoData: TextView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private var adapter by Delegates.notNull<MovieFavoriteAdapter>()
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var movieDatabase: AppDatabase
    private var mListMovie = arrayListOf<MovieEntity>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMovie = view.findViewById(R.id.rv_movie)
        tvNoData = view.findViewById(R.id.tv_no_data)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        swipeRefresh.isEnabled = false

        movieDatabase = AppDatabase.getInstance(view.context)

        mLayoutManager = LinearLayoutManager(view.context)
        rvMovie.setHasFixedSize(true)
        adapter = MovieFavoriteAdapter(mListMovie, this)
        rvMovie.layoutManager = mLayoutManager
        rvMovie.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        showDetailMovie()
    }

    override fun onMovieClickListener(movieId: String) {
        val intent = Intent(context, DetailMovieActivity::class.java)
        intent.putExtra(MOVIE_EXTRA, movieId)
        startActivity(intent)
    }

    private fun showDetailMovie() {
        mListMovie.clear()
        mListMovie.addAll(movieDatabase.movieDao().getAllMovie())
        adapter.notifyDataSetChanged()

        if (adapter.itemCount > 0) {
            rvMovie.visibility = View.VISIBLE
            tvNoData.visibility = View.GONE
        } else {
            rvMovie.visibility = View.GONE
            tvNoData.visibility = View.VISIBLE
        }
    }
}
