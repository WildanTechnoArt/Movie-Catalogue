package com.wildan.moviecatalogue.service

import android.content.Intent
import android.widget.RemoteViewsService
import com.wildan.moviecatalogue.widget.FavoriteStackRemoteViewsFactory

class StackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FavoriteStackRemoteViewsFactory(this.applicationContext, intent)
    }
}