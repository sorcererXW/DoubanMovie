package com.sorcererxw.doubanmovie.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.sorcererxw.doubanmovie.R;
import com.sorcererxw.doubanmovie.data.SimpleMovieBean;
import com.sorcererxw.doubanmovie.ui.activities.MovieDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @description:
 * @author: Sorcerer
 * @date: 2017/6/11
 */

public class DetailMovieHorizontalListAdapter
        extends RecyclerView.Adapter<DetailMovieHorizontalListAdapter.ViewHolder> {
    private Activity mActivity;

    private List<SimpleMovieBean> mList;

    public DetailMovieHorizontalListAdapter(Activity context) {
        mActivity = context;
        mList = new ArrayList<>();
    }

    public DetailMovieHorizontalListAdapter(Activity context,
                                            List<SimpleMovieBean> list) {
        mActivity = context;
        mList = list;
    }

    public void setData(List<SimpleMovieBean> list) {
        mList = list;
    }


    @Override
    public DetailMovieHorizontalListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                          int viewType) {
        return new DetailMovieHorizontalListAdapter.ViewHolder(LayoutInflater.from(mActivity)
                .inflate(R.layout.item_movie_horizontal_list_detail, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SimpleMovieBean movie = mList.get(position);

        runEnterAnimation(holder.itemView, position);

        Glide.with(mActivity)
                .load(movie.getImageUrl())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(holder.mPicture);

        holder.mTitle.setText(movie.getTitle());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mActivity, MovieDetailActivity.class);
            intent.putExtra("movie", movie);
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, holder.mPicture,
                            mActivity.getString(R.string.transition_name_movie_image));
            ActivityCompat.startActivity(mActivity, intent, options.toBundle());
        });

        if (movie.getRating() > 0) {
            holder.mRatingInfo.setText(String.valueOf(movie.getRating()) + " ★");
        } else {
            holder.mRatingInfo.setText(mActivity.getString(R.string.no_rating));
        }
    }

    private int mLastAnimatedPosition = -1;

    private void runEnterAnimation(View view, int position) {
        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            view.setAlpha(0);
            view.animate()
                    .alpha(1)
                    .setStartDelay(200 * position)
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(500)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.linearLayout_item_main_lists_item_container)
        LinearLayout mContainer;

        @BindView(R.id.imageView_item_main_lists_item)
        ImageView mPicture;

        @BindView(R.id.textView_item_main_lists_item_title)
        TextView mTitle;

        @BindView(R.id.textView_item_main_lists_item_rating_info)
        TextView mRatingInfo;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}