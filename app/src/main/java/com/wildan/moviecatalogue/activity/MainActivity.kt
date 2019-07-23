package com.wildan.moviecatalogue.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import android.view.Menu
import android.view.MenuItem

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.fragment.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bottom_navigation.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            bottom_navigation.selectedItemId = R.id.movie_menu
        }
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.content_view, fragment)
                .commit()
            return true
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.setting -> startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return true
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment: Fragment? = null

        when (p0.itemId) {
            R.id.movie_menu -> fragment = MovieFragment()
            R.id.tvshow_menu -> fragment = TvShowFragment()
            R.id.favorite_menu -> fragment = FavoriteFragment()
        }
        return loadFragment(fragment)
    }
}
