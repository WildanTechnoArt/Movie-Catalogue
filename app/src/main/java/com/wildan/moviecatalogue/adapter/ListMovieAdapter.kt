package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL

import kotlinx.android.synthetic.main.item_movie.view.*

class ListMovieAdapter(private val mClickListener: MovieAdapterListener) :
    RecyclerView.Adapter<ListMovieAdapter.MovieViewHolder>() {

    private var mMoviesList = ArrayList<MovieResult>()

    fun setData(items: ArrayList<MovieResult>) {
        mMoviesList.clear()
        mMoviesList.addAll(items)
        notifyDataSetChanged()
    }

    fun refreshAdapter(mMoviesList: List<MovieResult>) {
        this.mMoviesList.addAll(mMoviesList)
        notifyItemRangeChanged(0, this.mMoviesList.size)
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.item_movie, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mMoviesList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val mContext = holder.itemView.context

        holder.itemView.img_poster?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + mMoviesList[position].posterPath)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_title?.text = mMoviesList[position].title.toString()
        holder.itemView.tv_date?.text = DateFormat.getLongDate(mMoviesList[position].releaseDate.toString())
        holder.itemView.tv_popularity?.text =
            mContext?.resources?.getString(R.string.movie_popularity)?.let {
                String.format(
                    it,
                    mMoviesList[position].popularity.toString()
                )
            }
        holder.itemView.tv_rating?.text =
            mContext?.resources?.getString(R.string.movie_rating)?.let {
                String.format(
                    it,
                    mMoviesList[position].voteAverage.toString()
                )
            }

        holder.itemView.movie_item?.setOnClickListener {
            mClickListener.onItemClickListener(mMoviesList[position].id.toString(), "movie")
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}