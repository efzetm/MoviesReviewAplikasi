package com.example.topmovies.UI.details;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.topmovies.Model.CastModel;
import com.example.topmovies.Model.MoviesModel;
import com.example.topmovies.Model.RateResponse;
import com.example.topmovies.Model.TrailerModel;
import com.example.topmovies.R;
import com.example.topmovies.databinding.MovieDetailsBinding;
import com.example.topmovies.utils.Constants;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragmentX;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DetailsActivity extends AppCompatActivity {
    MovieDetailsBinding movieDetailsBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        movieDetailsBinding = DataBindingUtil.setContentView(this, R.layout.movie_details);

        YouTubePlayerSupportFragmentX frag =
                (YouTubePlayerSupportFragmentX) getSupportFragmentManager().findFragmentById(R.id.youtube_fragment);


        movieDetailsBinding.rateBar.setNumStars(10);


        movieDetailsBinding.rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                movieDetailsBinding.tvRateNumber.setVisibility(View.VISIBLE);
                movieDetailsBinding.tvRateNumber.setText(rating + "");
                movieDetailsBinding.btnRate.setEnabled(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    movieDetailsBinding.btnRate.getDefaultFocusHighlightEnabled();
                }


            }
        });


        DetailsAdapter detailsAdapter = new DetailsAdapter(this);


        movieDetailsBinding.rvCast.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        movieDetailsBinding.rvCast.setAdapter(detailsAdapter);

        MoviesModel moviesObject = getIntent().getParcelableExtra(Constants.MOVIE_INTENT_KEY);

        if (!moviesObject.getTitle().isEmpty())
            movieDetailsBinding.tvTitle.setText(moviesObject.getTitle());
        else {
            movieDetailsBinding.tvTitle.setText(moviesObject.getOriginalTitle());
        }

        movieDetailsBinding.tvHowWouldYouRate.setText("How would you rate " + moviesObject.getTitle() + " ?");

        movieDetailsBinding.tvDescription.setText(textHead("Description : " + moviesObject.getOverview(), 13));


        if (!TextUtils.isEmpty(moviesObject.getReleaseDate())) {
            movieDetailsBinding.tvReleaseDate.setText(textHead("Release Date : " + moviesObject.getReleaseDate(), 14));
        } else {
            movieDetailsBinding.tvReleaseDate.setText("Unkown");

        }
        movieDetailsBinding.tvAvgRate.setText(moviesObject.getVoteAverage() + "/10");
        Glide.with(this)
                .load(Constants.IMAGE_BASE_URL + moviesObject.getPosterPath())
                .transform(new MultiTransformation(new BlurTransformation()))
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        movieDetailsBinding.clDetailsBackground.setBackground(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }

                });

        if (!TextUtils.isEmpty(moviesObject.getBackdropPath())) {
            glideloader(moviesObject.getBackdropPath());

        } else {
            glideloader(moviesObject.getPosterPath());
        }


        DetailsActivityViewModel detailsActivityViewModel = ViewModelProviders.of(this).get(DetailsActivityViewModel.class);

        detailsActivityViewModel.getCastlist(moviesObject.getId());
        detailsActivityViewModel.getTrailerUrl(moviesObject.getId());



        detailsActivityViewModel.videoList.observe(this, new Observer<TrailerModel>() {
            @Override
            public void onChanged(TrailerModel trailerModel) {
                try {
                    movieDetailsBinding.trailerName.setText(trailerModel.getName());

                } catch (NullPointerException e) {

                    movieDetailsBinding.trailerName.setVisibility(View.GONE);

                    movieDetailsBinding.tvTrailerhead.setVisibility(View.GONE);

                }
                try {


                    frag.initialize(trailerModel.getKey(), new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                            youTubePlayer.cueVideo(trailerModel.getKey());
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                            movieDetailsBinding.llHideVideo.setVisibility(View.GONE);
                        }
                    });


                } catch (NullPointerException e) {


                    movieDetailsBinding.llHideVideo.setVisibility(View.GONE);


                }


            }
        });

        movieDetailsBinding.btnRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsActivityViewModel.getSessionId();
            }
        });

        detailsActivityViewModel.sessionId.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                detailsActivityViewModel.postRate(moviesObject.getId(), detailsActivityViewModel.sessionId.getValue(),
                        Float.toString(movieDetailsBinding.rateBar.getRating()));
            }
        });



        detailsActivityViewModel.rate.observe(this, new Observer<RateResponse>() {
            @Override
            public void onChanged(RateResponse rateResponse) {
                Toast.makeText(getApplicationContext(), movieDetailsBinding.rateBar.getRating() + " Stars! " + "Rated", Toast.LENGTH_LONG).show();
            }
        });


        detailsActivityViewModel.CastList.observe(this, new Observer<List<CastModel>>() {
            @Override
            public void onChanged(List<CastModel> castModels) {
                detailsAdapter.addCastList(castModels);


            }
        });

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                // network
                detailsActivityViewModel.getCastlist(moviesObject.getId());
                detailsActivityViewModel.getTrailerUrl(moviesObject.getId());
                Glide.with(getApplicationContext())
                        .load(Constants.IMAGE_BASE_URL + moviesObject.getPosterPath())
                        .transform(new MultiTransformation(new BlurTransformation()))
                        .into(new SimpleTarget<Drawable>() {
                            @Override
                            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                movieDetailsBinding.clDetailsBackground.setBackground(resource);
                            }
                        });


            }

            @Override
            public void onLost(Network network) {

            }
        };

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        }


    }


    private Spannable textHead(String text, int end) {
        Spannable spannable = new SpannableString(text);
        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.pink)), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(
                new RelativeSizeSpan(1.40f), 0, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannable;


    }

    private void glideloader(String imgurl) {

        RequestOptions castCircleCrop = new RequestOptions();
        castCircleCrop = castCircleCrop.transforms(new CenterCrop(), new RoundedCorners(120));

        Glide.with(this)
                .applyDefaultRequestOptions(castCircleCrop)
                .load(Constants.IMAGE_BASE_URL + imgurl)
                .into(movieDetailsBinding.ivMovieImage);
    }


}
