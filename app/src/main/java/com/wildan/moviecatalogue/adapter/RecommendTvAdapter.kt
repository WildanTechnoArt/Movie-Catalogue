package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.tv.TvShowResult
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.BACKDROP_URL
import kotlinx.android.synthetic.main.recommended_item.view.*

class RecommendTvAdapter(private val mClickListener: TvShowAdapterListener?) :
    RecyclerView.Adapter<RecommendTvAdapter.TvViewHolder>() {

    private var mTvShowList = ArrayList<TvShowResult>()

    fun setData(items: ArrayList<TvShowResult>) {
        mTvShowList.clear()
        mTvShowList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): TvViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.recommended_item, view, false)
        return TvViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mTvShowList.size
    }

    override fun onBindViewHolder(holder: TvViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val mContext = holder.itemView.context

        holder.itemView.img_backdrop?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(BACKDROP_URL + mTvShowList[position].backdropPath)
                    .placeholder(R.drawable.ic_image_placeholder_32dp)
                    .error(R.drawable.ic_error_image_32dp)
                    .into(it)
            }
        }

        holder.itemView.tv_movie_name?.text = mTvShowList[position].name.toString()
        holder.itemView.tv_rating?.text = mTvShowList[position].voteAverage.toString()

        holder.itemView.movie_item?.setOnClickListener {
            mClickListener?.onTvClickListener(mTvShowList[position].id.toString())
        }
    }

    inner class TvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}