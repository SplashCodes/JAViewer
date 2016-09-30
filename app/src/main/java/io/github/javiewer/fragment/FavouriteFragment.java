package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.MovieAdapter;

/**
 * Project: JAViewer
 */

public class FavouriteFragment extends RecyclerFragment<LinearLayoutManager> {
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(new MovieAdapter(JAViewer.CONFIGURATIONS.getStarredMovies(), this.getActivity()));
        mRefreshLayout.setEnabled(false);
    }
}
