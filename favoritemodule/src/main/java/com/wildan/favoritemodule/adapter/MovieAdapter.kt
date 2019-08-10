package com.wildan.favoritemodule.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wildan.favoritemodule.GlideApp
import com.wildan.favoritemodule.R
import com.wildan.favoritemodule.model.Movie
import com.wildan.favoritemodule.utils.DateFormat
import com.wildan.favoritemodule.utils.UtilsConstant.Companion.POSTER_URL
import kotlinx.android.synthetic.main.item_movie.view.*

import java.util.ArrayList

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val listMovies = ArrayList<Movie>()

    fun setListMovie(listMovies: ArrayList<Movie>) {
        this.listMovies.clear()
        this.listMovies.addAll(listMovies)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val context = holder.itemView.context

        holder.itemView.img_poster?.let {
            context?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + listMovies[position].posterPath)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_title?.text = listMovies[position].title.toString()
        holder.itemView.tv_date?.text = DateFormat.getLongDate(listMovies[position].releaseDate.toString())
        holder.itemView.tv_popularity?.text =
            context?.resources?.getString(R.string.movie_popularity)?.let {
                String.format(
                    it,
                    listMovies[position].popularity.toString()
                )
            }
        holder.itemView.tv_rating?.text =
            context?.resources?.getString(R.string.movie_rating)?.let {
                String.format(
                    it,
                    listMovies[position].voteAverage.toString()
                )
            }
    }

    override fun getItemCount(): Int {
        return listMovies.size
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}