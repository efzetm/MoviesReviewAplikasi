package com.example.topmovies.data.db;

import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.topmovies.Model.MoviesModel;

import java.util.List;

@androidx.room.Dao
public interface Dao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addMoviesList(List<MoviesModel> moviesList);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void addFavouriteMovie(MoviesModel moviesFav);


    @Query("SELECT * FROM movies")
    List<MoviesModel> getCashedMoviesList();


    @Query("SELECT * FROM movies WHERE favorite= 1")
    List<MoviesModel> getFavouriteMoviesList();


    @Query("SELECT favorite FROM movies WHERE mId=:id")
    int getFavValue(Long id);


    @Query("UPDATE movies SET favorite=:fav WHERE mId = :id ")
    void updateFavourite(int fav, Long id);


    @Query("DELETE FROM movies WHERE favorite=0")
    void deleteCachedMovies();


}
