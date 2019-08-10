package com.wildan.favoritemodule.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.wildan.favoritemodule.R
import com.wildan.favoritemodule.adapter.MovieAdapter
import com.wildan.favoritemodule.model.Movie
import com.wildan.favoritemodule.presenter.FavoriteMoviePresenter
import com.wildan.favoritemodule.view.FavoriteView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(), FavoriteView.View {

    private var adapter by Delegates.notNull<MovieAdapter>()
    private var mMovieList = arrayListOf<Movie>()
    private lateinit var presenter: FavoriteView.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepare()
    }

    private fun prepare(){
        presenter = FavoriteMoviePresenter(this)
        presenter.fetchMoviesData(this)

        rv_movie_list.layoutManager = LinearLayoutManager(this)
        rv_movie_list.setHasFixedSize(true)
        adapter = MovieAdapter()
        rv_movie_list.adapter = adapter
    }

    override fun showFavoriteMovie(movie: List<Movie>) {
        mMovieList.clear()
        mMovieList.addAll(movie)
        adapter.setListMovie(mMovieList)

        if(adapter.itemCount == 0){
            rv_movie_list.visibility = View.GONE
            tv_no_data.visibility = View.VISIBLE
        }
    }

    override fun handleError(throwable: String?) {
        Toast.makeText(this, throwable, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }
}
