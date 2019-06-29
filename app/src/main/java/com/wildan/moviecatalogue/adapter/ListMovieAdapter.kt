package com.wildan.moviecatalogue.adapter

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.activity.DetailActivity
import com.wildan.moviecatalogue.activity.DetailActivity.Companion.EXTRA
import com.wildan.moviecatalogue.model.Movie

class ListMovieAdapter(private val context: Context, private val movies: ArrayList<Movie>?) :
    RecyclerView.Adapter<ListMovieAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imgPoster: ImageView? = itemView.findViewById(R.id.img_poster)
        val tvTitle: TextView? = itemView.findViewById(R.id.tv_title)
        val tvDate: TextView? = itemView.findViewById(R.id.tv_date)
        val tvRating: TextView? = itemView.findViewById(R.id.tv_rating)
        val tvGenres: TextView? = itemView.findViewById(R.id.tv_genres)
        val itemDetail: ConstraintLayout? = itemView.findViewById(R.id.movie_item)

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, p1: Int): MovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_movie, viewGroup, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movies?.size ?: 0
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        holder.imgPoster?.let {
            GlideApp.with(context)
                .load(movies?.get(position)?.poster)
                .into(it)
        }

        holder.tvTitle?.text = movies?.get(position)?.title.toString()
        holder.tvDate?.text = movies?.get(position)?.date.toString()
        holder.tvRating?.text =
            String.format(context.resources.getString(R.string.movie_rating), movies?.get(position)?.rating.toString())

        holder.tvGenres?.text = movies?.get(position)?.genres.toString()
        holder.itemDetail?.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA, position.let { it1 -> movies?.get(it1) })
            context.startActivity(intent)
        }
    }
}