package com.wildan.moviecatalogue.adapter

interface MovieAdapterListener {
    fun onItemClickListener(movieId: String, movieType: String)
}