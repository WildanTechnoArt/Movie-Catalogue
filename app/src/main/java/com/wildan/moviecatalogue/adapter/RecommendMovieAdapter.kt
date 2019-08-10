package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.movie.MovieResult
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import kotlinx.android.synthetic.main.recommended_item.view.*

class RecommendMovieAdapter(private val mClickListener: MovieAdapterListener?) :
    RecyclerView.Adapter<RecommendMovieAdapter.MovieViewHolder>() {

    private var mMoviesList = ArrayList<MovieResult>()

    fun setData(items: ArrayList<MovieResult>) {
        mMoviesList.clear()
        mMoviesList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.recommended_item, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mMoviesList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val mContext = holder.itemView.context

        holder.itemView.img_backdrop?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(BACKDROP_URL + mMoviesList[position].backdropPath)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_movie_name?.text = mMoviesList[position].title.toString()
        holder.itemView.tv_rating?.text = mMoviesList[position].voteAverage.toString()

        holder.itemView.movie_item?.setOnClickListener {
            mClickListener?.onMovieClickListener(mMoviesList[position].id.toString())
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}