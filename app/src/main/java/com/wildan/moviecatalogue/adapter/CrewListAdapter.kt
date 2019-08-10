package com.wildan.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.credit.CrewData
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import kotlinx.android.synthetic.main.credit_item.view.*

class CrewListAdapter : RecyclerView.Adapter<CrewListAdapter.MovieViewHolder>() {

    private var mCrewList = ArrayList<CrewData>()

    fun setData(items: ArrayList<CrewData>) {
        mCrewList.clear()
        mCrewList.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(view: ViewGroup, p1: Int): MovieViewHolder {
        val mView = LayoutInflater.from(view.context).inflate(R.layout.credit_item, view, false)
        return MovieViewHolder(mView)
    }

    override fun getItemCount(): Int {
        return mCrewList.size
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        val mContext = holder.itemView.context

        holder.itemView.img_photo?.let {
            mContext?.let { it1 ->
                GlideApp.with(it1)
                    .load(POSTER_URL + mCrewList[position].profilePath)
                    .placeholder(R.drawable.ic_profile_placeholder_24dp)
                    .error(R.drawable.ic_profile_placeholder_24dp)
                    .into(it)
            }
        }

        holder.itemView.tv_name.text = mCrewList[position].name.toString()
        holder.itemView.tv_role.text = mCrewList[position].job.toString()
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}