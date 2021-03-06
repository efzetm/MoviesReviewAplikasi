package com.example.topmovies.data.network;

import com.example.topmovies.Model.RateResponse;
import com.example.topmovies.Model.SessionId;
import com.example.topmovies.utils.Constants;

import java.util.HashMap;
import java.util.Map;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
   private static ApiClient instance = null;
    private ApiInterface apiInterface;

    //singleton to take only one instance
    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;

    }



    private ApiClient() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    HttpUrl url = request.url()
                            .newBuilder()
                            .addQueryParameter(Constants.API_KEY_NAME, Constants.API_KEY_VALUE)
                            .build();
                    request = request.newBuilder().url(url).build();
                    return chain.proceed(request);

                })
                .addInterceptor(logging).build();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build();
        apiInterface = retrofit.create(ApiInterface.class);


    }


    public Call<MoviesResponseBody> getPopularRatedMovies(int page) {
        return apiInterface.getPopularRatedMovies(page);

    }

    public Call<MoviesResponseBody> getSearchResults(String query) {
        return apiInterface.getSearchResults(query);

    }

    public Call<CastsResponseBody> getCast(Long movieId) {
        return apiInterface.getCast(movieId);

    }

    public Call<TrailerResponseBody> getTrailer(Long movieId) {

        return apiInterface.getVideo(movieId);
    }


    public Call<MoviesResponseBody> getCategories(String catogeryId,int page) {

        Map<String, String> data = new HashMap<>();
        //sort
        data.put("sort_by", "popularity.desc");
        //catogery id
        data.put("with_genres", catogeryId);
        return apiInterface.getCatogries(data,page);


    }

    public Call<SessionId> getSessionId() {
        return apiInterface.getSessionId();

    }

    public Call<RateResponse> postRate(Long movieId, String sessionId, String rateValue) {

        return apiInterface.postRate(movieId, sessionId, rateValue);

    }


}
