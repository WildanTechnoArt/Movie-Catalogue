package com.wildan.moviecatalogue.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Binder
import android.os.Bundle
import android.widget.AdapterView
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.wildan.moviecatalogue.GlideApp
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.model.favorite.Favorite
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.wildan.moviecatalogue.utils.UtilsConstant.Companion.POSTER_URL
import com.wildan.moviecatalogue.widget.FavoriteWidget.Companion.EXTRA_ITEM

class FavoriteStackRemoteViewsFactory(private val context: Context, intent: Intent) :
    RemoteViewsService.RemoteViewsFactory {

    private var mCursor: Cursor? = null
    private var mAppWidgetId: Int = 0

    init {
        mAppWidgetId =
            intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
    }

    override fun onCreate() {}

    override fun onDataSetChanged() {
        if (mCursor != null) {
            mCursor?.close()
        }

        val identityToken: Long = Binder.clearCallingIdentity()

        mCursor = context.contentResolver.query(CONTENT_URI, null, null, null, null)

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun onDestroy() {
        if (mCursor != null) {
            mCursor?.close()
        }
    }

    override fun getCount(): Int {
        return if (mCursor == null) 0 else mCursor?.count ?: 0
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position == AdapterView.INVALID_POSITION ||
            mCursor == null || !mCursor!!.moveToPosition(position)
        ) {
            return null
        }

        val favorite: Favorite? = getItem(position)

        val rv = RemoteViews(context.packageName, R.layout.favorite_widget_item)

        val bmp: Bitmap? = GlideApp.with(context)
            .asBitmap()
            .load(POSTER_URL + favorite?.posterPath.toString())
            .placeholder(R.drawable.ic_image_placeholder_32dp)
            .error(R.drawable.ic_error_image_32dp)
            .submit()
            .get()

        rv.setImageViewBitmap(R.id.movie_poster, bmp)
        rv.setTextViewText(R.id.tv_movie_title, favorite?.title.toString())

        val extras = Bundle()
        extras.putString(EXTRA_ITEM, favorite?.title.toString())

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.movie_poster, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return if (mCursor?.moveToPosition(position)!!) mCursor!!.getLong(0) else position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun getItem(position: Int): Favorite? {
        if (!mCursor?.moveToPosition(position)!!) {
            throw IllegalStateException("Position invalid!")
        }

        return mCursor?.let { Favorite(it) }
    }
}