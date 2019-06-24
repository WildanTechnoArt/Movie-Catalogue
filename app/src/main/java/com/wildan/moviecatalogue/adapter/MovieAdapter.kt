package com.wildan.moviecatalogue.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.activity.DetailActivity
import com.wildan.moviecatalogue.activity.DetailActivity.Companion.EXTRA
import com.wildan.moviecatalogue.model.Movie

class MovieAdapter(private val context: Context, private val movies: ArrayList<Movie>?) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        }

        val viewHolder = ViewHolder(view)
        val movie = getItem(position) as Movie
        viewHolder.bind(movie, position)
        return view
    }

    override fun getItem(position: Int): Movie? {
        return movies?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return movies?.size ?: 0
    }

    inner class ViewHolder internal constructor(view: View?) {

        private var imgPoster: ImageView? = view?.findViewById(R.id.img_poster)
        private var tvTitle: TextView? = view?.findViewById(R.id.tv_title)
        private var tvDate: TextView? = view?.findViewById(R.id.tv_date)
        private var tvRating: TextView? = view?.findViewById(R.id.tv_rating)
        private var tvGenres: TextView? = view?.findViewById(R.id.tv_genres)
        private var btnDetail: Button? = view?.findViewById(R.id.btn_detail)
        
        @SuppressLint("SetTextI18n")
        internal fun bind(movie: Movie, position: Int?) {
            imgPoster?.let {
                GlideApp.with(context)
                    .load(movie.poster)
                    .into(it)
            }

            tvTitle?.text = movie.title.toString()
            tvDate?.text = movie.date.toString()
            tvRating?.text = "Rating: ${movie.rating.toString()}"
            tvGenres?.text = movie.genres.toString()
            btnDetail?.setOnClickListener {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra(EXTRA, position?.let { it1 -> movies?.get(it1) })
                context.startActivity(intent)
            }
        }
    }
}