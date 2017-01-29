package io.github.javiewer.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

/**
 * Project: JAViewer
 */
public class ExtendedAppBarFragment extends Fragment {
    private AppBarLayout mAppBarLayout;

    public void setAppBarLayout(AppBarLayout mAppBarLayout) {
        this.mAppBarLayout = mAppBarLayout;
    }

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }
}
