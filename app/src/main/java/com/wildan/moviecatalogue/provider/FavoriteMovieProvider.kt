package com.wildan.moviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.wildan.moviecatalogue.database.DatabaseContract.AUTHORITY
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.wildan.moviecatalogue.database.DatabaseContract.MovieColumns.Companion.ID
import com.wildan.moviecatalogue.database.DatabaseContract.TABLE_NAME
import com.wildan.moviecatalogue.database.DatabaseHelper

class FavoriteMovieProvider : ContentProvider() {

    private var dbHelper: DatabaseHelper? = null

    override fun onCreate(): Boolean {
        dbHelper = context?.let { DatabaseHelper(it) }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        var mSelection = selection
        var mSelectionArgs = selectionArgs

        val db = dbHelper!!.readableDatabase
        val match = uriMatcher.match(uri)

        val cursor: Cursor

        when (match) {
            MOVIES, MOVIES_WITH_ID -> {
                if (match == MOVIES_WITH_ID) {
                    val id = ContentUris.parseId(uri)
                    mSelection = String.format("%s = ?", ID)
                    mSelectionArgs = arrayOf(id.toString())
                }

                cursor = db.query(
                    TABLE_NAME, projection,
                    mSelection, mSelectionArgs, null, null, sortOrder
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri : $uri")
        }

        cursor.setNotificationUri(context!!.contentResolver, uri)

        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {

        val db = dbHelper!!.writableDatabase
        val match = uriMatcher.match(uri)
        val insertUri: Uri

        when (match) {
            MOVIES -> {
                val id = db.insert(TABLE_NAME, null, contentValues)
                if (id > 0) {
                    insertUri = ContentUris.withAppendedId(CONTENT_URI, id)
                } else {
                    throw android.database.SQLException("Failed to insert row into $uri")
                }
            }
            else -> throw UnsupportedOperationException("Unknown uri : $uri")
        }

        context!!.contentResolver.notifyChange(uri, null)

        return insertUri
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val mSelection: String?
        var mSelectionArgs = selectionArgs

        when (uriMatcher.match(uri)) {
            MOVIES -> mSelection = selection ?: "1"
            MOVIES_WITH_ID -> {
                val id = ContentUris.parseId(uri)
                mSelection = String.format("%s = ?", ID)
                mSelectionArgs = arrayOf(id.toString())
            }
            else -> throw IllegalArgumentException("Illegal delete URI")
        }

        val db = dbHelper!!.writableDatabase
        val delete = db.delete(TABLE_NAME, mSelection, mSelectionArgs)

        if (delete > 0) {
            context!!.contentResolver.notifyChange(uri, null)
        }

        return delete
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val db = dbHelper!!.writableDatabase
        val match = uriMatcher.match(uri)

        val update: Int

        when (match) {
            MOVIES -> update = db.update(TABLE_NAME, contentValues, selection, selectionArgs)
            else -> throw UnsupportedOperationException("Unknown uri : $uri")
        }

        context!!.contentResolver.notifyChange(uri, null)

        return update
    }

    companion object {

        private const val MOVIES = 100
        private const val MOVIES_WITH_ID = 101

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, TABLE_NAME, MOVIES)
            uriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", MOVIES_WITH_ID)
        }
    }
}