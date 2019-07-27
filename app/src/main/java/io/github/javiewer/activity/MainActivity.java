package io.github.javiewer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.AbstractBadgeableDrawerItem;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialize.util.UIUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.Guideline;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DataSource;
import io.github.javiewer.fragment.ActressesFragment;
import io.github.javiewer.fragment.ExtendedAppBarFragment;
import io.github.javiewer.fragment.HomeFragment;
import io.github.javiewer.fragment.PopularFragment;
import io.github.javiewer.fragment.ReleasedFragment;
import io.github.javiewer.fragment.genre.GenreTabsFragment;
import io.github.javiewer.network.BasicService;
import io.github.javiewer.view.SimpleSearchView;

public class MainActivity extends SecureActivity {

    public static final int ID_HOME = 1;
    public static final int ID_FAV = 2;
    public static final int ID_POPULAR = 3;
    public static final int ID_RELEASED = 4;
    public static final int ID_ACTRESSES = 5;
    public static final int ID_GENRE = 6;
    public static final int ID_GITHUB = 7;

    public static final Map<Integer, Class<? extends Fragment>> FRAGMENTS = new HashMap<Integer, Class<? extends Fragment>>() {{
        put(ID_HOME, HomeFragment.class);
        put(ID_POPULAR, PopularFragment.class);
        put(ID_RELEASED, ReleasedFragment.class);
        put(ID_ACTRESSES, ActressesFragment.class);
        put(ID_GENRE, GenreTabsFragment.class);
    }};

    public Fragment currentFragment;

    @BindView(R.id.nav_view)
    public NavigationView mNavigationView;

    @BindView(R.id.app_bar)
    public AppBarLayout mAppBarLayout;

    @BindView(R.id.search_view)
    public SimpleSearchView mSearchView;

    @BindView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @BindView(R.id.toolbar)
    public Toolbar mToolbar;


    int idOfDrawerItem = ID_HOME;
    private FragmentManager fragmentManager;
    private Bundle savedInstanceState;

    private Drawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        if (JAViewer.CONFIGURATIONS == null) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
            return;
        }

        JAViewer.recreateService();

        this.savedInstanceState = savedInstanceState;

        setSupportActionBar(mToolbar);

        initFragments();

        buildDrawer();

    }

    public void buildDrawer() {

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withIdentifier(ID_HOME).withName("主页").withIcon(R.drawable.ic_menu_home).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(ID_FAV).withName("收藏夹").withTag("Fav").withIcon(R.drawable.ic_menu_star).withIconTintingEnabled(true).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(ID_RELEASED).withName("已发布").withIcon(R.drawable.ic_menu_released).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(ID_POPULAR).withName("热门").withIcon(R.drawable.ic_menu_popular).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(ID_ACTRESSES).withName("女优").withIcon(R.drawable.ic_menu_actresses).withIconTintingEnabled(true),
                        new PrimaryDrawerItem().withIdentifier(ID_GENRE).withName("类别").withIcon(R.drawable.ic_menu_genre).withIconTintingEnabled(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withIdentifier(ID_GITHUB).withName("GitHub").withTag("Github").withIcon(R.drawable.ic_menu_github).withIconTintingEnabled(true).withSelectable(false)
                )
                .withSelectedItem(ID_HOME)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        idOfDrawerItem = (int) drawerItem.getIdentifier();

                        switch ((int) drawerItem.getIdentifier()) {
                            case ID_GITHUB: {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/SplashCodes/JAViewer/releases"));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                break;
                            }
                            case ID_FAV: {
                                Intent intent = new Intent(MainActivity.this, FavouriteActivity.class);
                                startActivity(intent);
                                break;
                            }
                            default:
                                if (drawerItem instanceof AbstractBadgeableDrawerItem) {
                                    setFragment(((int) drawerItem.getIdentifier()), ((AbstractBadgeableDrawerItem) drawerItem).getName().getText());
                                }

                                break;
                        }
                        return false;
                    }
                })
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Guideline guideline = result.getHeader().findViewById(R.id.guideline_status_bar);
            guideline.setGuidelineBegin(UIUtils.getStatusBarHeight(this, true));
        }

        this.mDrawer = result;

        TextView mTextSource = mDrawer.getHeader().findViewById(R.id.text_view_source);
        mTextSource.setText(JAViewer.CONFIGURATIONS.getDataSource().toString());

        MaterialButton mButtonSwitch = mDrawer.getHeader().findViewById(R.id.btn_switch_source);
        mButtonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSwitchSource();
            }
        });

        if (this.savedInstanceState != null) {
            mDrawer.setSelection(savedInstanceState.getInt("SelectedDrawerItemId", ID_HOME));
        } else {
            mDrawer.setSelection(ID_HOME);
        }
    }

    public void initFragments() {
        this.fragmentManager = getSupportFragmentManager();

        if (this.savedInstanceState != null) {
            String tag = this.savedInstanceState.getString("CurrentFragment");
            this.currentFragment = fragmentManager.findFragmentByTag(tag);
            return;
        }

        FragmentTransaction transaction = this.fragmentManager.beginTransaction();
        for (Class<? extends Fragment> fragmentClass : FRAGMENTS.values()) {
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
        this.setFragment(fragmentManager.findFragmentByTag(FRAGMENTS.get(id).getSimpleName()), title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("CurrentFragment", this.currentFragment.getClass().getSimpleName());
        outState.putInt("SelectedDrawerItemId", this.idOfDrawerItem);
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

    public void restart() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void onSwitchSource() {
        DataSource[] ds = JAViewer.DATA_SOURCES.toArray(new DataSource[0]);
        String[] items = new String[ds.length];
        for (int i = 0; i < ds.length; i++) {
            items[i] = ds[i].toString();
        }
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("选择数据源")
                .setItems(items, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataSource newSource = JAViewer.DATA_SOURCES.get(which);
                        if (newSource.equals(JAViewer.getDataSource())) {
                            return;
                        }

                        JAViewer.CONFIGURATIONS.setDataSource(newSource);
                        JAViewer.CONFIGURATIONS.save();
                        restart();
                    }
                }).create();
        dialog.show();

    }

}
