package io.github.javiewer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.fragment.HomeFragment;
import io.github.javiewer.fragment.MovieFragment;
import io.github.javiewer.fragment.PopularFragment;
import io.github.javiewer.fragment.ReleasedFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Map<Integer, Fragment> fragments;
    private FragmentManager fragmentManager;

    public Fragment currentFragment;

    @Bind(R.id.nav_view)
    public NavigationView mNavigationView;

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

        this.fragmentManager = getSupportFragmentManager();
        this.setFragment(R.id.nav_home);
    }

    private void setFragment(int id) {
        Fragment fragment = fragments.get(id);
        Fragment old = this.currentFragment;

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
                Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", "搜索结果：" + query);
                bundle.putString("query", "search/" + query);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
