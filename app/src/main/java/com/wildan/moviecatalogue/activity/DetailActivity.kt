package com.wildan.moviecatalogue.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.model.Movie
import com.wildan.moviecatalogue.R
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var movieData: Movie

    companion object {
        const val EXTRA = "movie_data"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
        showMovieData()
    }

    private fun init() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showMovieData() {
        movieData = intent.getParcelableExtra(EXTRA)
        GlideApp.with(this)
            .load(movieData.poster)
            .into(img_poster)
        tv_title.text = movieData.title.toString()
        tv_date.text = movieData.date.toString()
        tv_rating.text = String.format(resources.getString(R.string.movie_rating), movieData.rating.toString())
        tv_genres.text = movieData.genres.toString()
        tv_description.text = movieData.description.toString()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
