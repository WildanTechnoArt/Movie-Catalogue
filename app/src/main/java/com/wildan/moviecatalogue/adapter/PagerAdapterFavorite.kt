package com.wildan.moviecatalogue.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.wildan.moviecatalogue.fragment.MovieFavoriteFragment
import com.wildan.moviecatalogue.fragment.TvShowFavoriteFragment

class PagerAdapterFavorite(fm: FragmentManager, behavior: Int) :
    FragmentStatePagerAdapter(fm, behavior) {

    private val pages = listOf(MovieFavoriteFragment(), TvShowFavoriteFragment())

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }
}