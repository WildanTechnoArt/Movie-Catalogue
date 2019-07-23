package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.database.TvShowEntity
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL

import kotlinx.android.synthetic.main.item_movie.view.*

class TvFavoriteAdapter(
    private val mListMovie: ArrayList<TvShowEntity>,
    private val mClickListener: TvShowAdapterListener
) :
    RecyclerView.Adapter<TvFavoriteAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.item_movie, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mListMovie.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val mContext = holder.itemView.context

        holder.itemView.img_poster?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + mListMovie[position].tvPoster)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_title?.text = mListMovie[position].tvName.toString()
        holder.itemView.tv_date?.text = DateFormat.getLongDate(mListMovie[position].tvDate.toString())
        holder.itemView.tv_popularity?.text =
            mContext?.resources?.getString(R.string.movie_popularity)?.let {
                String.format(
                    it,
                    mListMovie[position].tvPopularity.toString()
                )
            }
        holder.itemView.tv_rating?.text =
            mContext?.resources?.getString(R.string.movie_rating)?.let {
                String.format(
                    it,
                    mListMovie[position].tvRating.toString()
                )
            }

        holder.itemView.movie_item?.setOnClickListener {
            mClickListener.onItemClickListener(mListMovie[position].tvId.toString(), "movie")
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}