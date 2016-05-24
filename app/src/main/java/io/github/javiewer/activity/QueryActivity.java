package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import io.github.javiewer.R;
import io.github.javiewer.fragment.QueryFragment;

public class QueryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);

        Bundle bundle = this.getIntent().getExtras();

        getSupportActionBar().setTitle(bundle.getString("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(4);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        QueryFragment fragment = new QueryFragment();
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
