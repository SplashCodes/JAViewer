package io.github.javiewer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.fragment.ActressesFragment;
import io.github.javiewer.fragment.GenreFragment;
import io.github.javiewer.fragment.HomeFragment;
import io.github.javiewer.fragment.PopularFragment;
import io.github.javiewer.fragment.ReleasedFragment;
import io.github.javiewer.fragment.ToolbarNoElevationFragment;
import io.github.javiewer.network.AVMO;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, Fragment> fragments;
    private FragmentManager fragmentManager;

    public Fragment currentFragment;

    @Bind(R.id.nav_view)
    public NavigationView mNavigationView;

    @Bind(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

    public static DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading()
            .cacheInMemory()
            .cacheOnDisc()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .delayBeforeLoading(1000)
            .displayer(new FadeInBitmapDisplayer(500)) // default
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("即将请求储存空间权限，用于图片缓存功能，减少重复流量消耗")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .create();
            dialog.show();
        }

        initFragments();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
    }

    public void initFragments() {
        this.fragments = new HashMap<>();
        Fragment fragment = new HomeFragment();
        this.fragments.put(R.id.nav_home, fragment);

        fragment = new PopularFragment();
        this.fragments.put(R.id.nav_popular, fragment);

        fragment = new ReleasedFragment();
        this.fragments.put(R.id.nav_released, fragment);

        fragment = new ActressesFragment();
        this.fragments.put(R.id.nav_actresses, fragment);

        fragment = new GenreFragment();
        this.fragments.put(R.id.nav_genre, fragment);

        this.fragmentManager = getSupportFragmentManager();
        this.setFragment(R.id.nav_home);
    }

    private void setFragment(int id) {
        Fragment fragment = fragments.get(id);
        Fragment old = this.currentFragment;

        if (old == fragment) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (old != null) {
            transaction.hide(old);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.content, fragment);
        } else {
            transaction.show(fragment);
        }

        transaction.commit();

        this.currentFragment = fragment;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fragment instanceof ToolbarNoElevationFragment) {
                mAppBarLayout.setElevation(0);
            } else {
                mAppBarLayout.setElevation(4 * getResources().getDisplayMetrics().density);
            }
        }

        getSupportActionBar().setTitle(mNavigationView.getMenu().findItem(id).getTitle());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        final SearchView mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, MovieListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", query + " 的搜索结果");
                try {
                    bundle.putString("query", AVMO.BASE_URL + AVMO.LANGUAGE_NODE + "/search/" + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException ignored) {
                }
                intent.putExtras(bundle);

                MainActivity.this.startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = fragments.get(id);

        if (fragment != null) {
            setFragment(id);
        }

        if (id == R.id.nav_github) {
            Uri uri = Uri.parse("https://github.com/SplashCodes/JAViewer/releases");
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(it);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
