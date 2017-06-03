package com.sorcererxw.doubanmovie.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.sorcererxw.doubanmovie.R;
import com.sorcererxw.doubanmovie.api.douban.DoubanClient;
import com.sorcererxw.doubanmovie.ui.views.MovieHorizontalListView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.movieHorizontalListView_main_intheaters)
    MovieHorizontalListView mInTheaterView;

    @BindView(R.id.movieHorizontalListView_main_comingsoon)
    MovieHorizontalListView mComingSoonView;

    @BindView(R.id.movieHorizontalListView_main_top250)
    MovieHorizontalListView mTop250View;

    @BindView(R.id.movieHorizontalListView_main_usbox)
    MovieHorizontalListView mUsBox;

    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle("");
        mInTheaterView.init("In Theater", "More", null, DoubanClient.getInstance().inTheaters());
//        mComingSoonView
//                .init("Coming Soon", "More", null, DoubanClient.getInstance().comingSoon(0, 10));
//        mTop250View.init("Top250", "More", null, DoubanClient.getInstance().top250(0, 10));
//        mUsBox.init("Us Box", "More", null, DoubanClient.getInstance().usBox());
    }
}
