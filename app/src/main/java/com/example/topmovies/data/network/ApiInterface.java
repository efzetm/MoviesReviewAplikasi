package com.example.topmovies.data.network;

import com.example.topmovies.Model.RateResponse;
import com.example.topmovies.Model.SessionId;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("trending/movie/day")
    Call<MoviesResponseBody> getTrendingMovies();


    @GET("authentication/guest_session/new")
    Call<SessionId> getSessionId();


    @POST("movie/{movie_id}/rating")
    Call<RateResponse> postRate(@Path(value = "movie_id", encoded = true) Long movieId,
                                @Query("guest_session_id") String sessionId, @Query("value") String value);


    @GET("discover/movie")
    Call<MoviesResponseBody> getCatogries(@QueryMap Map<String, String> quires,@Query("page") int page);


    @GET("movie/popular")
    Call<MoviesResponseBody> getPopularRatedMovies(@Query("page") int page);


    @GET("search/movie")
    Call<MoviesResponseBody> getSearchResults(@Query("query") String query);


    @GET("movie/{movie_id}/credits")
    Call<CastsResponseBody> getCast(@Path(value = "movie_id", encoded = true) Long movieId);


    @GET("movie/{video_id}/videos")
    Call<TrailerResponseBody> getVideo(@Path(value = "video_id", encoded = true) Long movieId);

}
