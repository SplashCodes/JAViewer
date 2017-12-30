package io.github.javiewer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.Properties;
import io.github.javiewer.R;
import io.github.javiewer.adapter.NavigationSpinnerAdapter;
import io.github.javiewer.adapter.item.DataSource;
import io.github.javiewer.fragment.ExtendedAppBarFragment;
import io.github.javiewer.network.BasicService;
import io.github.javiewer.view.SimpleSearchView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    public Fragment currentFragment;

    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;

    @BindView(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

    @BindView(R.id.search_view)
    public SimpleSearchView mSearchView;

    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    int positionOfSpinner = 0;
    int idOfMenuItem = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        JAViewer.recreateService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.fragmentManager = getSupportFragmentManager();
        initFragments(savedInstanceState);

        if (savedInstanceState != null) {
            idOfMenuItem = savedInstanceState.getInt("MenuSelectedItemId", R.id.nav_home);
        }
        mNavigationView.setNavigationItemSelectedListener(this);
        MenuItem selectedItem = mNavigationView.getMenu().findItem(idOfMenuItem);
        mNavigationView.setCheckedItem(selectedItem.getItemId());
        onNavigationItemSelected(selectedItem);

        final Spinner spinner = (Spinner) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_spinner);
        ArrayAdapter<DataSource> adapter = new NavigationSpinnerAdapter<>(this, R.layout.nav_spinner_item, JAViewer.DATA_SOURCES);
        adapter.setDropDownViewResource(R.layout.view_drop_down);
        spinner.setAdapter(adapter);
        spinner.setSelection(positionOfSpinner = JAViewer.DATA_SOURCES.indexOf(JAViewer.getDataSource()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                final DataSource newSource = JAViewer.DATA_SOURCES.get(position);
                if (newSource.equals(JAViewer.getDataSource())) {
                    return;
                }

                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否切换到" + newSource.getName() + "数据源？")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                spinner.setSelection(positionOfSpinner);
                            }
                        })
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                positionOfSpinner = position;
                                JAViewer.CONFIGURATIONS.setDataSource(newSource);
                                JAViewer.CONFIGURATIONS.save();
                                restart();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spinner.setSelection(positionOfSpinner);
                            }
                        })
                        .show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


    }

    public void initFragments(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("CurrentFragment");
            this.currentFragment = fragmentManager.findFragmentByTag(tag);
            return;
        }

        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        for (int id : JAViewer.FRAGMENTS.keySet()) {
            Class<? extends Fragment> fragmentClass = JAViewer.FRAGMENTS.get(id);
            try {
                Fragment fragment = fragmentClass.getConstructor(new Class[0]).newInstance();
                transaction.add(R.id.content, fragment, fragmentClass.getSimpleName()).hide(fragment);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        transaction.commit();
        this.fragmentManager.executePendingTransactions();
    }

    @SuppressWarnings("ConstantConditions")
    private void setFragment(Fragment fragment, CharSequence title) {
        getSupportActionBar().setTitle(title);

        Fragment old = this.currentFragment;

        if (old == fragment) {
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (old != null) {
            transaction.hide(old);
        }
        transaction.show(fragment);
        transaction.commit();

        this.currentFragment = fragment;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (fragment instanceof ExtendedAppBarFragment) {
                mAppBarLayout.setElevation(0);
            } else {
                mAppBarLayout.setElevation(4 * getResources().getDisplayMetrics().density);
            }
        }
    }

    private void setFragment(int id, CharSequence title) {
        this.setFragment(fragmentManager.findFragmentByTag(JAViewer.FRAGMENTS.get(id).getSimpleName()), title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CurrentFragment", this.currentFragment.getClass().getSimpleName());
        outState.putInt("MenuSelectedItemId", this.idOfMenuItem);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
            return;
        }

        moveTaskToBack(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);
        mSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    startActivity(MovieListActivity.newIntent(MainActivity.this, query + " 的搜索结果", JAViewer.getDataSource().getLink() + BasicService.LANGUAGE_NODE + "/search/" + URLEncoder.encode(query, "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    return false;
                }
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        idOfMenuItem = id;

        switch (id) {
            case R.id.nav_github: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case R.id.nav_favourite: {
                Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                startActivity(intent);
                break;
            }
            default:
                setFragment(id, item.getTitle());
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

}
