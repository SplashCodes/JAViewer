package io.github.javiewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ActressPaletteAdapter;
import io.github.javiewer.adapter.MovieHeaderAdapter;
import io.github.javiewer.adapter.ScreenshotAdapter;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.network.AVMO;
import io.github.javiewer.network.provider.AVMOProvider;
import io.github.javiewer.view.ViewUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {

    public String detailUrl;

    @Bind(R.id.toolbar_layout_background)
    ImageView mToolbarLayoutBackgroud;

    @Bind(R.id.movie_content)
    View mContent;

    @Bind(R.id.movie_progress_bar)
    ProgressBar mProgressBar;

    @Bind(R.id.fab)
    FloatingActionButton mFab;

    @Bind(R.id.genre_flow_layout)
    FlowLayout mFlowLayout;

    String code;

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

        code = bundle.getString("code");

        detailUrl = bundle.getString("detail");

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieActivity.this, DownloadActivity.class);
                Bundle arguments = new Bundle();
                arguments.putString("keyword", code);
                intent.putExtras(arguments);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        Call<ResponseBody> call = AVMO.INSTANCE.get(this.detailUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MovieDetail detail;
                try {
                    detail = AVMOProvider.parseMoviesDetail(response.body().string());

                    //getSupportActionBar().setTitle(movie.title);

                    ImageLoader.getInstance().displayImage(detail.coverUrl, mToolbarLayoutBackgroud);

                    displayInfo(detail);
                } catch (IOException e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void displayInfo(MovieDetail detail) {
        //Changing visibility
        mProgressBar.setVisibility(View.GONE);
        mContent.setVisibility(View.VISIBLE);

        //Slide Up Animation
        mContent.setY(mContent.getY() + 120);
        mContent.setAlpha(0);
        mContent.animate().translationY(0).setDuration(500).alpha(1);

        //Info
        {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.headers_recycler_view);
            TextView mText = (TextView) findViewById(R.id.header_empty_text);
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_header);

            if (detail.headers.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
                ViewUtil.alignIconToView(mIcon, mText);
            } else {
                mRecyclerView.setAdapter(new MovieHeaderAdapter(detail.headers, this, mIcon));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                mRecyclerView.setNestedScrollingEnabled(false);
            }
        }

        //Screenshots
        {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.screenshots_recycler_view);
            TextView mText = (TextView) findViewById(R.id.screenshots_empty_text);
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_screenshots);

            if (detail.screenshots.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
                ViewUtil.alignIconToView(mIcon, mText);
            } else {
                mRecyclerView.setAdapter(new ScreenshotAdapter(detail.screenshots, this, mIcon));
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
                mRecyclerView.setNestedScrollingEnabled(false);
            }
        }

        //Actress
        {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.actresses_recycler_view);
            TextView mText = (TextView) findViewById(R.id.actresses_empty_text);
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_actresses);

            if (detail.actresses.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
                ViewUtil.alignIconToView(mIcon, mText);
            } else {
                mRecyclerView.setAdapter(new ActressPaletteAdapter(detail.actresses, this, mIcon));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView.setNestedScrollingEnabled(false);
            }
        }

        {
            boolean first = true;
            for (final Genre genre : detail.genres) {
                View view = getLayoutInflater().inflate(R.layout.card_genre_movie, mFlowLayout, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (genre.getLink() != null) {
                            Intent intent = new Intent(MovieActivity.this, MovieListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("title", genre.getName());
                            bundle.putString("query", genre.getLink());

                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    }
                });
                TextView textView = (TextView) view.findViewById(R.id.chip_genre_name);
                textView.setText(genre.getName());

                mFlowLayout.addView(view);

                if (first) {
                    ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_genre);
                    ViewUtil.alignIconToView(mIcon, view);
                    first = false;
                }
            }
        }
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
