package com.example.topmovies.UI.main;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.topmovies.Model.MoviesModel;
import com.example.topmovies.R;
import com.example.topmovies.databinding.ActivityMainBinding;
import com.example.topmovies.databinding.ListItemMoviesBinding;
import com.example.topmovies.utils.Constants;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private Context context;
    private OnItemClicked onItemClicked;
    MoviesActivityViewModel viewModel;
    ActivityMainBinding activityMainBinding;


    public MoviesAdapter(Context context, MoviesActivityViewModel mainActivityViewModel,
                         ActivityMainBinding activityMainBinding, OnItemClicked onItemClicked) {
        this.context = context;
        this.onItemClicked = onItemClicked;
        viewModel = mainActivityViewModel;
        this.activityMainBinding = activityMainBinding;
    }


    List<MoviesModel> moviesList = new ArrayList<>();


    @NonNull
    @Override

    public MoviesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ListItemMoviesBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item_movies, parent, false);
        return new ViewHolder(binding);

    }


    @Override
    public void onBindViewHolder(@NonNull MoviesAdapter.ViewHolder holder, int position) {
        MoviesModel moviesModel = moviesList.get(position);
        holder.binding.tvAvgRate.setText(moviesModel.getVoteAverage() + "");


        if (viewModel.getFavValue(moviesModel.getId()) == 1) {
            holder.binding.btnFav.setLiked(true);

        } else {
            holder.binding.btnFav.setLiked(false);

        }


        if (!moviesModel.getTitle().isEmpty()) {
            holder.binding.tvTitle.setText(moviesModel.getTitle());

        } else {

            holder.binding.tvTitle.setText(moviesModel.getOriginalTitle());
        }


        if (!TextUtils.isEmpty(moviesModel.getBackdropPath())) {
            glideloader(moviesModel.getBackdropPath(), holder);

        } else {

            glideloader(moviesModel.getPosterPath(), holder);
        }

        try {
            holder.binding.tvReleaseDate.setText(releaseYear(moviesModel.getReleaseDate()));
        } catch (Exception e) {
            holder.binding.tvReleaseDate.setText("Unknown");

        }
    }


    private String releaseYear(String date) {
        return date.substring(0, 4);
    }

    void glideloader(String original, ViewHolder holder) {
        // to apply rounded image
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(Constants.IMAGE_BASE_URL + original)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.binding.pbMovies.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.binding.pbMovies.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.binding.ivPhoto);

    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ListItemMoviesBinding binding;

        public ViewHolder(@NonNull ListItemMoviesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            //animation instance
            RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate);
            binding.btnFav.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    binding.star.startAnimation(rotateAnimation);
                    binding.btnFav.setLiked(true);
                    viewModel.addfavouriteMovie(moviesList.get(getAdapterPosition()));
                    // set favourite value to 1 is liked(data base)
                    viewModel.updateFavouriteValue(moviesList.get(getAdapterPosition()).getId(), 1);


                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    binding.star.startAnimation(rotateAnimation);
                    viewModel.updateFavouriteValue(moviesList.get(getAdapterPosition()).getId(), 0);
                    // if we are in favourite and the list is empty make recycler view invisible and show no favourites
                    if (activityMainBinding.tvHeadName.getText() == "Favourites") {


                            viewModel.getFavouritesMoviesList();



                    }

                }
            });


            itemView.setOnClickListener(v -> onItemClicked.onListItemCLicked(moviesList.get(getAdapterPosition())));
        }

    }


    public void addMoviesList(List<MoviesModel> moviesList) {
        this.moviesList = moviesList;
        notifyDataSetChanged();
    }




    public void loadMore(List<MoviesModel> moviesList) {
        this.moviesList.addAll(moviesList);
        notifyDataSetChanged();
    }


    public void clear() {
        this.moviesList.clear();
        notifyDataSetChanged();

    }


}
