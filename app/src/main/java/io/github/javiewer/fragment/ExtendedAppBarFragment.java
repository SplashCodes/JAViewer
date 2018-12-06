package io.github.javiewer.fragment;

import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;

/**
 * Project: JAViewer
 */
public class ExtendedAppBarFragment extends Fragment {
    private AppBarLayout mAppBarLayout;

    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    public void setAppBarLayout(AppBarLayout mAppBarLayout) {
        this.mAppBarLayout = mAppBarLayout;
    }
}
