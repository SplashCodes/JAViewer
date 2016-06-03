package io.github.javiewer.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.MovieDetailAdapter;
import io.github.javiewer.network.Network;
import io.github.javiewer.network.HtmlHelper;
import io.github.javiewer.network.wrapper.MovieDetailWrapper;
import io.github.javiewer.network.wrapper.ScreenshotWrapper;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MovieActivity extends AppCompatActivity {

    public String detailUrl;

    @Bind(R.id.toolbar_layout_background)
    ImageView mToolbarLayoutBackgroud;

    @Bind(R.id.movie_recycler_view)
    RecyclerView mRecyclerView;

    MovieDetailAdapter mAdapter;

    List<ScreenshotWrapper> screenshots = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(bundle.getString("title"));

        mRecyclerView.setAdapter(mAdapter = new MovieDetailAdapter(screenshots, this));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);
        mRecyclerView.setNestedScrollingEnabled(false);

        detailUrl = bundle.getString("detail");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.SOURCE_URL)
                .build();

        Network network = retrofit.create(Network.class);

        Call<ResponseBody> call = network.query(this.detailUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MovieDetailWrapper movie;
                try {
                    movie = HtmlHelper.parseMoviesDetail(response.body().string());

                    //getSupportActionBar().setTitle(movie.title);

                    ImageLoader.getInstance().displayImage(movie.coverUrl, mToolbarLayoutBackgroud);

                    screenshots.clear();
                    screenshots.addAll(movie.screenshots);

                    mAdapter.onInit(movie);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
