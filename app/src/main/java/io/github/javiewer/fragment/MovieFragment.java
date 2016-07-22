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
import io.github.javiewer.network.HtmlHelper;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MovieFragment extends RecyclerFragment<LinearLayoutManager> {


    public List<Movie> movies = new ArrayList<>();

    public SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    public EndlessOnScrollListener mScrollListener;

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

        mRecyclerView.addOnScrollListener(mScrollListener = new EndlessOnScrollListener(getLayoutManager()) {
            @Override
            public void onLoad(final long loadingTime, final boolean refresh) {
                final int page = currentPage;
                Call<ResponseBody> call = getCall(page + 1);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (loadingTime == latestLoadingTime && (!mRefreshLayout.isRefreshing() || refresh)) {
                            try {
                                List<Movie> wrappers = HtmlHelper.parseMovies(response.body().string());

                                if (refresh) {
                                    movies.clear();
                                }

                                int pos = movies.size();

                                if (pos > 0) {
                                    pos--;
                                }

                                movies.addAll(wrappers);
                                getAdapter().notifyItemChanged(pos, wrappers.size());

                                currentPage++;
                            } catch (Throwable e) {
                                onFailure(call, e);
                            }
                        }

                        loading = false;
                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        loading = false;
                        mRefreshLayout.setRefreshing(false);
                        t.printStackTrace();
                    }
                });
            }
        });

        mRefreshLayout.setOnRefreshListener(mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.loading = true;
                mScrollListener.reset();
                mScrollListener.onLoad(mScrollListener.latestLoadingTime = System.currentTimeMillis(), true);
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

    public abstract Call<ResponseBody> getCall(int page);

    public static abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

        public LinearLayoutManager mLayoutManager;

        public boolean loading = false;

        private int loadThreshold = 5;
        public int currentPage = 0;

        public long latestLoadingTime;

        public EndlessOnScrollListener(LinearLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        public void reset() {
            loading = false;
            loadThreshold = 5;
            currentPage = 0;
        }

        public void onLoad(long loadingTime, boolean refresh) {

        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + loadThreshold)) {
                onLoad(latestLoadingTime = System.currentTimeMillis(), false);
                loading = true;
            }
        }
    }
}
