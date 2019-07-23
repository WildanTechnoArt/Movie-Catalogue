package com.wildan.moviecatalogue.database

import androidx.room.*

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movieEntity: MovieEntity): Long

    @Query("SELECT * FROM tMovie")
    fun getAllMovie(): Array<MovieEntity>

    @Query("SELECT * FROM tMovie WHERE movieId = :movieId")
    fun getMovieById(movieId: String): Array<MovieEntity>

    @Query("DELETE FROM tMovie WHERE movieId = :movieId")
    fun deleteMovie(movieId: String)

}