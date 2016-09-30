package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.MovieAdapter;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.listener.EndlessOnScrollListener;
import io.github.javiewer.network.provider.AVMOProvider;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;

public abstract class MovieFragment extends RecyclerFragment<LinearLayoutManager> {

    public List<Movie> movies = new ArrayList<>();

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(new ScaleInAnimationAdapter(new MovieAdapter(movies, this.getActivity())));
        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.refresh();
            }
        });

        this.addOnScrollListener(new EndlessOnScrollListener<Movie>(getLayoutManager(), mRefreshLayout, this.movies) {
            @Override
            public Call<ResponseBody> newCall(int page) {
                return MovieFragment.this.newCall(page);
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<Movie> wrappers = AVMOProvider.parseMovies(response.string());

                int pos = movies.size();

                if (pos > 0) {
                    pos--;
                }

                movies.addAll(wrappers);
                getAdapter().notifyItemChanged(pos, wrappers.size());
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                mRefreshListener.onRefresh();
            }
        });
    }

    public abstract Call<ResponseBody> newCall(int page);
}
