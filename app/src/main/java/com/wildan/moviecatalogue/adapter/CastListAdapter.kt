package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.credit.CastData
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import kotlinx.android.synthetic.main.credit_item.view.*

class CastListAdapter : RecyclerView.Adapter<CastListAdapter.MovieViewHolder>() {

    private var mCastList = ArrayList<CastData>()

    fun setData(items: ArrayList<CastData>) {
        mCastList.clear()
        mCastList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.credit_item, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mCastList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val mContext = holder.itemView.context

        holder.itemView.img_photo?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + mCastList[position].profilePath)
                    .placeholder(R.drawable.ic_profile_placeholder_24dp)
                    .error(R.drawable.ic_profile_placeholder_24dp)
                    .into(it)
            }
        }

        holder.itemView.tv_name.text = mCastList[position].name.toString()
        holder.itemView.tv_role.text = mCastList[position].character.toString()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}