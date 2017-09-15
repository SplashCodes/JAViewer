package io.github.javiewer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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

    @BindView(R.id.bottom_nav_favourite)
    BottomNavigationView mNav;

    MenuItem menuItem;

    public static ViewPagerAdapter mAdapter;

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
            if (menuItem != null) {
                menuItem.setChecked(false);
            } else {
                mNav.getMenu().getItem(0).setChecked(false);
            }
            mNav.getMenu().getItem(position).setChecked(true);
            menuItem = mNav.getMenu().getItem(position);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);

        FavouriteFragment fragment = new FavouriteMovieFragment();
        mAdapter.addFragment(fragment, "作品");
        fragment = new FavouriteActressFragment();
        mAdapter.addFragment(fragment, "女优");
        mAdapter.notifyDataSetChanged();

        mNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mNav.setElevation(ViewUtil.dpToPx(8));
        }*/
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
