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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.Properties;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DataSource;
import io.github.javiewer.fragment.ExtendedAppBarFragment;
import io.github.javiewer.network.BasicService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;

    public Fragment currentFragment;

    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;

    @BindView(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

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
        ArrayAdapter<DataSource> adapter = new ArrayAdapter<>(this, R.layout.nav_spinner_item, JAViewer.DATA_SOURCES);
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

        //ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));

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

    public void initFragments(Bundle savedInstanceState) {
        //FragmentTransaction transaction = this.fragmentManager.beginTransaction();

        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("CurrentFragment");
            this.currentFragment = fragmentManager.findFragmentByTag(tag);
            /*for (Fragment fragment : fragmentManager.getFragments()) {
                transaction.hide(fragment);
            }

            transaction.show(this.currentFragment);
            transaction.commit();*/
            return;
        }

        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        for (int id : JAViewer.FRAGMENTS.keySet()) {
            Class<? extends Fragment> fragmentClass = JAViewer.FRAGMENTS.get(id);
            try {
                Fragment fragment = (Fragment) fragmentClass.getConstructor(new Class[0]).newInstance();
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

        /*
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (old != null) {
            transaction.hide(old);
        }

        if (!fragment.isAdded()) {
            transaction.add(R.id.content, fragment);
        } else {
            transaction.show(fragment);
        }*/
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        /*for (Fragment f : fragmentManager.getFragments()) {
            transaction.hide(f);
        }*/
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
        idOfMenuItem = id;

        switch (id) {
            case R.id.nav_github: {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases"));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case R.id.nav_donate: {
                String qrCode = "https://qr.alipay.com/a6x05027ymf6n8kl0qkoa54";
                String scheme = "alipays://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2Fa6x05027ymf6n8kl0qkoa54%3F_s%3Dweb-other";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(qrCode));
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + qrCode + "?_s=web-other&_t=" + System.currentTimeMillis()));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
