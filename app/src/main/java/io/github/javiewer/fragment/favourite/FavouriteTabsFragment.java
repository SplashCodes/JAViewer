package io.github.javiewer.fragment.favourite;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.fragment.ExtendedAppBarFragment;

public class FavouriteTabsFragment extends ExtendedAppBarFragment {

    @BindView(R.id.favourite_tabs)
    public TabLayout mTabLayout;

    @BindView(R.id.favourite_view_pager)
    public ViewPager mViewPager;

    public static ViewPagerAdapter mAdapter;

    public FavouriteTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        FavouriteFragment fragment = new FavouriteMovieFragment();
        mAdapter.addFragment(fragment, "作品");
        fragment = new FavouriteActressFragment();
        mAdapter.addFragment(fragment, "女优");

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    public static void update() {
        if (mAdapter != null) {
            for (int i = 0; i < mAdapter.getCount(); i++) {
                ((FavouriteFragment) mAdapter.getItem(i)).update();
            }
        }
    }
}
