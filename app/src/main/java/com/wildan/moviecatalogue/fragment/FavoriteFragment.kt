package com.wildan.moviecatalogue.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.adapter.PagerAdapterFavorite

class FavoriteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = view.findViewById(R.id.tabs_movie) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.movie)))
        tabLayout.addTab(tabLayout.newTab().setText(resources.getString(R.string.tv_show)))

        val viewPager = view.findViewById(R.id.container) as ViewPager

        val pageAdapter = fragmentManager?.let {
            PagerAdapterFavorite(
                it, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        }

        viewPager.adapter = pageAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    companion object {
        const val BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1
    }
}
