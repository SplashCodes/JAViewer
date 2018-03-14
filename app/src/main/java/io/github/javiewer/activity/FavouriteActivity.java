package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.fragment.favourite.FavouriteActressFragment;
import io.github.javiewer.fragment.favourite.FavouriteFragment;
import io.github.javiewer.fragment.favourite.FavouriteMovieFragment;

public class FavouriteActivity extends AppCompatActivity {

    @BindView(R.id.favourite_view_pager)
    public ViewPager mViewPager;

    @BindView(R.id.favourite_bottom_bar)
    BottomBar mBottomBar;

    MenuItem menuItem;

    public static ViewPagerAdapter mAdapter;

    private OnTabSelectListener mOnTabSelectedListener = new OnTabSelectListener() {
        @Override
        public void onTabSelected(@IdRes int tabId) {
            Log.i("tabid", String.valueOf(tabId));
            switch (tabId) {
                case R.id.tab_fav_actress:
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.tab_fav_movie:
                    mViewPager.setCurrentItem(0);
                    break;
            }

            return;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_fav_actresses:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_fav_movie:
                    mViewPager.setCurrentItem(0);
            }
            return false;
        }
    };

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mBottomBar.selectTabAtPosition(position, true);
            mBottomBar.getShySettings().showBar();
            /*if (menuItem != null) {
                menuItem.setChecked(false);
            } else {
                mNav.getMenu().getItem(0).setChecked(false);
            }
            mNav.getMenu().getItem(position).setChecked(true);
            menuItem = mNav.getMenu().getItem(position);*/
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        FavouriteFragment fragment = new FavouriteMovieFragment();
        mAdapter.addFragment(fragment, "作品");
        fragment = new FavouriteActressFragment();
        mAdapter.addFragment(fragment, "女优");
        mAdapter.notifyDataSetChanged();

        //mNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mBottomBar.setOnTabSelectListener(mOnTabSelectedListener, true);

        /*NestedScrollView mScrollView = (NestedScrollView) findViewById(R.id.favourite_scroll_view);
        mScrollView.setFillViewport(true);*/
    }

    public static void update() {
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                ((FavouriteFragment) mAdapter.getItem(i)).update();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
