package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.javiewer.R;
import io.github.javiewer.fragment.MovieListFragment;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Bundle bundle = this.getIntent().getExtras();

        getSupportActionBar().setTitle(bundle.getString("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4 * getResources().getDisplayMetrics().density);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        MovieListFragment fragment = new MovieListFragment();
        fragment.setArguments(bundle);
        transaction.replace(R.id.content_query, fragment);
        transaction.commit();
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
