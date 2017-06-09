package com.sorcererxw.doubanmovie.ui.activities;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.materialize.util.UIUtils;
import com.sorcererxw.doubanmovie.R;
import com.sorcererxw.doubanmovie.api.douban.DoubanClient;
import com.sorcererxw.doubanmovie.data.MovieBean;
import com.sorcererxw.doubanmovie.data.SimpleMovieBean;
import com.sorcererxw.doubanmovie.ui.adapters.CelebrityAdapter;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailActivity extends AppCompatActivity {

    @BindView(R.id.cardView_movie_detail_summary)
    CardView mSummaryCard;

    @BindView(R.id.cardView_movie_detail_celebrity)
    CardView mCelebrityCard;

    @BindView(R.id.toolbar_movie_detail)
    Toolbar mToolbar;

    @BindView(R.id.imageView_movie_detail_poster)
    ImageView mPoster;

    @BindView(R.id.recyclerView_movie_detail_celebrity)
    RecyclerView mRecyclerView;

    @BindView(R.id.textView_movie_detail_summary)
    TextView mSummary;

    @BindView(R.id.imageView_movie_detail_bg)
    ImageView mBackgroundImage;

    @BindView(R.id.linearLayout_movie_detail_content_container)
    ViewGroup mContentContainer;

    @BindView(R.id.collapsingToolbarLayout_movie)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.textView_movie_detail_info)
    TextView mInfo;

    @BindView(R.id.textView_movie_detail_title)
    TextView mTitleText;
    @BindView(R.id.textView_movie_detail_original_title)
    TextView mOriginalTitleText;

    private MovieBean mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        SimpleMovieBean movie = getIntent().getParcelableExtra("movie");
        Glide.with(this).load(movie.getImageUrl()).into(mPoster);

        mTitleText.setText(movie.getTitle());
        if (movie.getOriginTitle().equals(movie.getTitle()) || movie.getOriginTitle().isEmpty()) {
            mOriginalTitleText.setVisibility(View.GONE);
        } else {
            mOriginalTitleText.setText(movie.getOriginTitle());
        }

//        StatusBarUtil.setTranslucent(this);

        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(movie.getTitle());

        mCollapsingToolbarLayout.setTitleEnabled(false);
        mCollapsingToolbarLayout.setTitle(movie.getTitle());

        StatusBarUtil.setTransparent(this);

        Glide.with(this).asBitmap().load(movie.getImageUrl()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource,
                                        Transition<? super Bitmap> transition) {
                tintToolbar(resource);
            }
        });


        DoubanClient.getInstance().movie(movie.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movieBean -> {
                    mMovie = movieBean;
                    init();
                    mInfo.animate().alpha(1).setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).start();

                    mSummaryCard.setTranslationY(mSummaryCard.getHeight());
                    mSummaryCard.animate().alpha(1).translationY(0).setDuration(500)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).start();

                    mCelebrityCard
                            .setTranslationY(mCelebrityCard.getHeight());
                    mCelebrityCard.animate().alpha(1).translationY(0).setDuration(500)
                            .setStartDelay(300)
                            .setInterpolator(new AccelerateDecelerateInterpolator()).start();
                }, Timber::e);

    }

    private void init() {
        mSummary.setText(mMovie.getSummary());

        mInfo.setText(Html.fromHtml(
                Stream.of(Arrays.asList(
                        new Pair<>(mMovie.getAka(), getString(R.string.movie_detail_aka)),
                        new Pair<>(mMovie.getGenres(), getString(R.string.movie_detail_genre)),
                        new Pair<>(mMovie.getCountries(), getString(R.string.movie_detail_country))
                ))
                        .filter(pair -> pair.first.size() > 0)
                        .map(pair -> "<b>" + pair.second + ": </b>" + Stream.of(pair.first)
                                .collect(Collectors.joining(" / ")))
                        .collect(Collectors.joining("<br/>"))
                )
        );

        Glide.with(this).load(mMovie.getImageUrl())
                .transition(new DrawableTransitionOptions().crossFade(1000))
                .into(mBackgroundImage);

        CelebrityAdapter adapter = new CelebrityAdapter(this, Stream.concat(
                Stream.of(mMovie.getDirectors()).map(simpleCelebrityBean ->
                        new Pair<>(simpleCelebrityBean, getString(R.string.role_director))),
                Stream.of(mMovie.getCasts()).map(simpleCelebrityBean ->
                        new Pair<>(simpleCelebrityBean, getString(R.string.role_cast))))
                .toList());

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                       RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int pos = parent.getChildAdapterPosition(view);
                if (pos == 0) {
                    outRect.right = (int) UIUtils.convertDpToPixel(4, MovieDetailActivity.this);
                    outRect.left = (int) UIUtils.convertDpToPixel(16, MovieDetailActivity.this);
                } else if (pos == state.getItemCount() - 1) {
                    outRect.right = (int) UIUtils.convertDpToPixel(16, MovieDetailActivity.this);
                    outRect.left = (int) UIUtils.convertDpToPixel(4, MovieDetailActivity.this);
                } else {
                    outRect.right = (int) UIUtils.convertDpToPixel(4, MovieDetailActivity.this);
                    outRect.left = (int) UIUtils.convertDpToPixel(4, MovieDetailActivity.this);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void tintToolbar(Bitmap bitmap) {

        final int defaultPrimaryColor =
                ContextCompat.getColor(MovieDetailActivity.this, R.color.colorPrimary);
        new Palette.Builder(bitmap).generate(palette -> {
//            palette.getSwatches()
            if (palette.getDominantSwatch() != null) {
//                mToolbar.setBackgroundColor(palette.getDominantColor(defaultPrimaryColor));
            }
        });
    }

}
