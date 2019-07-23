package com.wildan.moviecatalogue.database

import androidx.room.*

@Dao
interface TvShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTvShow(TvShowEntity: TvShowEntity): Long

    @Query("SELECT * FROM tTvShow")
    fun getAllTvShow(): Array<TvShowEntity>

    @Query("SELECT * FROM tTvShow WHERE tvId = :tvShowId")
    fun getTvShowById(tvShowId: String): Array<TvShowEntity>

    @Query("DELETE FROM tTvShow WHERE tvId = :tvShowId")
    fun deleteTvShow(tvShowId: String)

}