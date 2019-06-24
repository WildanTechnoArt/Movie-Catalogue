package com.wildan.moviecatalogue.activity

import android.content.res.TypedArray
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wildan.moviecatalogue.model.Movie
import com.wildan.moviecatalogue.adapter.MovieAdapter
import com.wildan.moviecatalogue.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var listTitle: Array<String>? = null
    private var listDate: Array<String>? = null
    private var listRating: Array<String>? = null
    private var listGenres: Array<String>? = null
    private var listDescription: Array<String>? = null
    private lateinit var listPoster: TypedArray
    private var movies: ArrayList<Movie>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepare()
        addItem()

        val adapter = MovieAdapter(this, movies)
        lv_movie.adapter = adapter
    }

    private fun prepare() {
        listTitle = resources.getStringArray(R.array.data_title)
        listDate = resources.getStringArray(R.array.data_date)
        listRating = resources.getStringArray(R.array.data_rating)
        listGenres = resources.getStringArray(R.array.data_genres)
        listDescription = resources.getStringArray(R.array.data_description)
        listPoster = resources.obtainTypedArray(R.array.data_poster)
    }

    private fun addItem() {
        movies = ArrayList()

        for (i in 0 until (listTitle?.size ?: 0)) {
            val movie = Movie()
            movie.poster = listPoster.getResourceId(i, -1)
            movie.title = listTitle?.get(i).toString()
            movie.date = listDate?.get(i).toString()
            movie.rating = listRating?.get(i).toString()
            movie.genres = listGenres?.get(i).toString()
            movie.description = listDescription?.get(i).toString()
            movies?.add(movie)
        }
    }
}
