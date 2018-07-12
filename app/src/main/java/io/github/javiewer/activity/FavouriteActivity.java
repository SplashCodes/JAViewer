package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.fragment.favourite.FavouriteActressFragment;
import io.github.javiewer.fragment.favourite.FavouriteFragment;
import io.github.javiewer.fragment.favourite.FavouriteMovieFragment;

public class FavouriteActivity extends AppCompatActivity {

    @BindView(R.id.favourite_view_pager)
    AHBottomNavigationViewPager mViewPager;

    @BindView(R.id.app_bar_fav)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.bottom_navigation)
    AHBottomNavigation mBottomNav;

    @BindView(R.id.toolbar_fav)
    Toolbar mToolbar;

    @BindColor(R.color.colorPrimary)
    int mColorPrimary;

    public static ViewPagerAdapter mAdapter;

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            mBottomNav.setCurrentItem(position);
            mBottomNav.restoreBottomNavigation();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPagingEnabled(true);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        FavouriteFragment fragment = new FavouriteMovieFragment();
        mAdapter.addFragment(fragment, "作品");
        fragment = new FavouriteActressFragment();
        mAdapter.addFragment(fragment, "女优");
        mAdapter.notifyDataSetChanged();

        AHBottomNavigationAdapter navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.nav_favourite);
        navigationAdapter.setupWithBottomNavigation(mBottomNav);
        mBottomNav.setTranslucentNavigationEnabled(true);
        mBottomNav.setAccentColor(mColorPrimary);
        mBottomNav.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);

        mBottomNav.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (!wasSelected) {
                    mViewPager.setCurrentItem(position);
                    return true;
                }

                return false;
            }
        });
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
