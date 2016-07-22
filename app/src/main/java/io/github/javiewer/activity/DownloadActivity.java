package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.fragment.DownloadFragment;

public class DownloadActivity extends AppCompatActivity {

    @Bind(R.id.download_toolbar)
    public Toolbar mToolbar;

    @Bind(R.id.download_tabs)
    public TabLayout mTabLayout;

    @Bind(R.id.download_view_pager)
    public ViewPager mViewPager;

    public String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        this.keyword = this.getIntent().getExtras().getString("keyword");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(this.keyword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment fragment;

        fragment = new DownloadFragment();
        bundle = (Bundle) bundle.clone();
        bundle.putString("provider", "btso");
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "BTSO");

        fragment = new DownloadFragment();
        bundle = (Bundle) bundle.clone();
        bundle.putString("provider", "torrentkitty");
        fragment.setArguments(bundle);
        adapter.addFragment(fragment, "Torrent Kitty");

        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
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
