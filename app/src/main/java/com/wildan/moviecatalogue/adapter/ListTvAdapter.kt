package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.utils.DateFormat
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL

import kotlinx.android.synthetic.main.item_movie.view.*

class ListTvAdapter(private val mListener: TvShowAdapterListener?) :
    RecyclerView.Adapter<ListTvAdapter.MovieViewHolder>() {

    private val tvShowList = ArrayList<TvShowResult>()

    fun setData(items: ArrayList<TvShowResult>) {
        tvShowList.clear()
        tvShowList.addAll(items)
        notifyDataSetChanged()
    }

    fun refreshAdapter(tvShowList: List<TvShowResult>) {
        this.tvShowList.addAll(tvShowList)
        notifyItemRangeChanged(0, this.tvShowList.size)
    }

    fun clearMovie(){
        tvShowList.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.item_movie, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return tvShowList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        val mContext = holder.itemView.context

        holder.itemView.img_poster?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + tvShowList[position].posterPath)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_title?.text = tvShowList[position].name.toString()
        holder.itemView.tv_date?.text = DateFormat.getLongDate(tvShowList[position].firstAirDate.toString())
        holder.itemView.tv_popularity?.text =
            mContext?.resources?.getString(R.string.movie_popularity)?.let {
                String.format(
                    it,
                    tvShowList[position].popularity.toString()
                )
            }
        holder.itemView.tv_rating?.text =
            mContext?.resources?.getString(R.string.movie_rating)
                ?.let { String.format(it, tvShowList[position].voteAverage.toString()) }

        holder.itemView.movie_item?.setOnClickListener {
            mListener?.onTvClickListener(tvShowList[position].id.toString())
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}