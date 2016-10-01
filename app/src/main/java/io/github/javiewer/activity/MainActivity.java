package io.github.javiewer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.Configurations;
import io.github.javiewer.JAViewer;
import io.github.javiewer.Properties;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DataSource;
import io.github.javiewer.fragment.ActressesFragment;
import io.github.javiewer.fragment.FavouriteFragment;
import io.github.javiewer.fragment.GenreTabsFragment;
import io.github.javiewer.fragment.HomeFragment;
import io.github.javiewer.fragment.NoToolbarElevation;
import io.github.javiewer.fragment.PopularFragment;
import io.github.javiewer.fragment.ReleasedFragment;
import io.github.javiewer.network.BasicService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SparseArray<Fragment> fragments;
    private FragmentManager fragmentManager;

    public Fragment currentFragment;

    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;

    @BindView(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        JAViewer.CONFIGURATIONS = Configurations.load(new File(this.getExternalFilesDir(null), "configurations.json"));
        JAViewer.recreateService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.getMenu().getItem(0).setChecked(true);

        Spinner spinner = (Spinner) mNavigationView.getHeaderView(0).findViewById(R.id.nav_header_spinner);
        ArrayAdapter<DataSource> adapter = new ArrayAdapter<>(this, R.layout.nav_spinner_item, JAViewer.DATA_SOURCES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(JAViewer.DATA_SOURCES.indexOf(JAViewer.getDataSource()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final DataSource newSource = JAViewer.DATA_SOURCES.get(position);
                if (newSource.equals(JAViewer.getDataSource())) {
                    return;
                }

                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("是否切换到" + newSource.getName() + "数据源？")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JAViewer.CONFIGURATIONS.setDataSource(newSource);
                                JAViewer.CONFIGURATIONS.save();
                                restart();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initFragments();

        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

        Request request = new Request.Builder()
                .url("https://raw.githubusercontent.com/SplashCodes/JAViewer/master/properties.json")
                .build();
        JAViewer.HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Properties properties = JAViewer.parseJson(Properties.class, response.body().string());
                if (properties != null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            handleProperties(properties);
                        }
                    });
                }
            }
        });
    }

    public void handleProperties(Properties properties) {
        int currentVersion;
        try {
            currentVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Hacked???");
        }

        if (properties.getLatestVersionCode() > 0 && currentVersion < properties.getLatestVersionCode()) {

            String message = "新版本：" + properties.getLatestVersion();
            if (properties.getChangelog() != null) {
                message += "\n\n更新日志：\n\n" + properties.getChangelog() + "\n";
            }

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("发现更新")
                    .setMessage(message)
                    .setNegativeButton("忽略更新", null)
                    .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases")));
                        }
                    })
                    .create();
            dialog.show();
        }
    }

    public void initFragments() {
        this.fragments = new SparseArray<>();
        Fragment fragment = new HomeFragment();
        this.fragments.put(R.id.nav_home, fragment);

        fragment = new PopularFragment();
        this.fragments.put(R.id.nav_popular, fragment);

        fragment = new ReleasedFragment();
        this.fragments.put(R.id.nav_released, fragment);

        fragment = new ActressesFragment();
        this.fragments.put(R.id.nav_actresses, fragment);

        fragment = new GenreTabsFragment();
        this.fragments.put(R.id.nav_genre, fragment);

        fragment = new FavouriteFragment();
        this.fragments.put(R.id.nav_favourite, fragment);

        this.fragmentManager = getSupportFragmentManager();
        this.setFragment(R.id.nav_home);
    }

    @SuppressWarnings("ConstantConditions")
    private void setFragment(Fragment fragment, CharSequence title) {
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
            if (fragment instanceof NoToolbarElevation) {
                mAppBarLayout.setElevation(0);
            } else {
                mAppBarLayout.setElevation(4 * getResources().getDisplayMetrics().density);
            }
        }

        getSupportActionBar().setTitle(title);
    }


    private void setFragment(int id) {
        this.setFragment(fragments.get(id), mNavigationView.getMenu().findItem(id).getTitle());
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

        switch (id) {
            case R.id.nav_github:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases")));
                break;
            default:
                Fragment fragment = fragments.get(id);

                if (fragment != null) {
                    setFragment(id);
                }
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void restart() {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
