package io.github.javiewer.fragment.genre;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.adapter.ViewPagerAdapter;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.fragment.ExtendedAppBarFragment;
import io.github.javiewer.network.provider.AVMOProvider;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreTabsFragment extends ExtendedAppBarFragment {

    @BindView(R.id.genre_tabs)
    public TabLayout mTabLayout;

    @BindView(R.id.genre_view_pager)
    public ViewPager mViewPager;

    @BindView(R.id.genre_progress_bar)
    public ProgressBar mProgressBar;

    public ViewPagerAdapter mAdapter;

    public GenreTabsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        Call<ResponseBody> call = JAViewer.SERVICE.getGenre();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressBar.setVisibility(View.GONE);
                try {
                    LinkedHashMap<String, List<Genre>> genres = AVMOProvider.parseGenres(response.body().string());

                    GenreFragment fragment;
                    for (String title : genres.keySet()) {
                        fragment = new GenreFragment();
                        fragment.getGenres().addAll(genres.get(title));
                        mAdapter.addFragment(fragment, title);
                    }

                    mAdapter.notifyDataSetChanged();

                    mTabLayout.setVisibility(View.VISIBLE);
                } catch (Throwable e) {
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
