package com.wildan.moviecatalogue.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast
import com.wildan.moviecatalogue.R
import com.wildan.moviecatalogue.service.StackWidgetService
import android.content.ComponentName

class FavoriteWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {

        const val TOAST_ACTION = "com.wildan.moviecatalogue.TOAST_ACTION"
        const val EXTRA_ITEM = "com.wildan.moviecatalogue.EXTRA_ITEM"
        const val UPDATE_WIDGET = "com.wildan.moviecatalogue.UPDATE_WIDGET"

        internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {

            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val views = RemoteViews(context.packageName, R.layout.favorite_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, FavoriteWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            val toastPendingIntent =
                PendingIntent.getBroadcast(context, 0, toastIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action != null) {

            if (intent.action.equals(TOAST_ACTION)) {
                val viewIndex = intent.getStringExtra(EXTRA_ITEM)
                Toast.makeText(context, viewIndex, Toast.LENGTH_SHORT).show()
            }

            if (intent.action.equals(UPDATE_WIDGET)) {
                val gm = AppWidgetManager.getInstance(context)
                val ids = gm.getAppWidgetIds(context?.let { ComponentName(it, FavoriteWidget::class.java) })
                gm.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
            }

        }

        super.onReceive(context, intent)
    }
}