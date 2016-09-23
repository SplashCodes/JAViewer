package io.github.javiewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ScreenshotAdapter;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.adapter.item.Screenshot;
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

    String code;


    List<Screenshot> screenshots = new ArrayList<>();

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

                    screenshots.clear();
                    screenshots.addAll(detail.screenshots);

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
            TextView mCodeText = (TextView) findViewById(R.id.info_text_code);
            TextView mDateText = (TextView) findViewById(R.id.info_text_date);
            TextView mDurationText = (TextView) findViewById(R.id.info_text_duration);
            mCodeText.setText(detail.code);
            mDateText.setText(detail.date);
            mDurationText.setText(detail.duration);
        }

        //Screenshots
        {
            RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.screenshots_recycler_view);
            TextView mText = (TextView) findViewById(R.id.screenshots_text);
            ImageView mIcon = (ImageView) findViewById(R.id.movie_icon_photo);

            mRecyclerView.setAdapter(new ScreenshotAdapter(screenshots, this, mIcon));
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerView.setNestedScrollingEnabled(false);

            if (screenshots.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mText.setVisibility(View.VISIBLE);
            }

            ViewUtil.alignIconToView(mIcon, mText);
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
