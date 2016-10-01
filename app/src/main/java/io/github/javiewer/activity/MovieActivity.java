package io.github.javiewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wefika.flowlayout.FlowLayout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ActressPaletteAdapter;
import io.github.javiewer.adapter.MovieHeaderAdapter;
import io.github.javiewer.adapter.ScreenshotAdapter;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.fragment.FavouriteFragment;
import io.github.javiewer.network.provider.AVMOProvider;
import io.github.javiewer.view.ViewUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieActivity extends AppCompatActivity {

    public Movie movie;

    @BindView(R.id.toolbar_layout_background)
    ImageView mToolbarLayoutBackground;

    @BindView(R.id.movie_content)
    View mContent;

    @BindView(R.id.movie_progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @BindView(R.id.genre_flow_layout)
    FlowLayout mFlowLayout;

    MenuItem mStarButton;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        movie = (Movie) bundle.getSerializable("movie");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(movie.title);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieActivity.this, DownloadActivity.class);
                Bundle arguments = new Bundle();
                arguments.putString("keyword", movie.getCode());
                intent.putExtras(arguments);
                startActivity(intent);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        Call<ResponseBody> call = JAViewer.SERVICE.get(this.movie.getLink());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                MovieDetail detail;
                try {
                    detail = AVMOProvider.parseMoviesDetail(response.body().string());
                    ImageLoader.getInstance().displayImage(detail.coverUrl, mToolbarLayoutBackground, JAViewer.DISPLAY_IMAGE_OPTIONS);
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
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_header);

            if (detail.headers.isEmpty()) {
                TextView mText = (TextView) findViewById(R.id.header_empty_text);
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
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_screenshots);

            if (detail.screenshots.isEmpty()) {
                TextView mText = (TextView) findViewById(R.id.screenshots_empty_text);
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
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_actresses);

            if (detail.actresses.isEmpty()) {
                TextView mText = (TextView) findViewById(R.id.actresses_empty_text);
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
                ViewUtil.alignIconToView(mIcon, mText);
            } else {
                mRecyclerView.setAdapter(new ActressPaletteAdapter(detail.actresses, this, mIcon));
                mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                mRecyclerView.setNestedScrollingEnabled(false);
            }
        }

        //Genre
        {
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_genre);

            if (detail.genres.isEmpty()) {
                mFlowLayout.setVisibility(View.GONE);
                TextView mText = (TextView) findViewById(R.id.genre_empty_text);
                mText.setVisibility(View.VISIBLE);
                ViewUtil.alignIconToView(mIcon, mText);
            } else {
                for (int i = 0; i < detail.genres.size(); i++) {
                    final Genre genre = detail.genres.get(i);
                    View view = getLayoutInflater().inflate(R.layout.card_genre_movie, mFlowLayout, false);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (genre.getLink() != null) {
                                startActivity(MovieListActivity.newIntent(MovieActivity.this, genre.getName(), genre.getLink()));
                            }
                        }
                    });
                    TextView textView = (TextView) view.findViewById(R.id.chip_genre_name);
                    textView.setText(genre.getName());
                    mFlowLayout.addView(view);

                    if (i == 0) {
                        ViewUtil.alignIconToView(mIcon, view);
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movie, menu);

        mStarButton = menu.findItem(R.id.action_star);

        {
            if (JAViewer.CONFIGURATIONS.getStarredMovies().contains(movie)) {
                mStarButton.setIcon(R.drawable.ic_menu_star);
                mStarButton.setTitle("取消收藏");
            }
        }

        mStarButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (JAViewer.CONFIGURATIONS.getStarredMovies().contains(movie)) {
                    JAViewer.CONFIGURATIONS.getStarredMovies().remove(movie);
                    mStarButton.setIcon(R.drawable.ic_menu_star_border);
                    Snackbar.make(mContent, "已取消收藏", Snackbar.LENGTH_LONG).show();
                    mStarButton.setTitle("收藏");
                } else {
                    JAViewer.CONFIGURATIONS.getStarredMovies().add(movie);
                    mStarButton.setIcon(R.drawable.ic_menu_star);
                    Snackbar.make(mContent, "已收藏", Snackbar.LENGTH_LONG).show();
                    mStarButton.setTitle("取消收藏");
                }
                JAViewer.CONFIGURATIONS.save();
                FavouriteFragment.update();

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}
