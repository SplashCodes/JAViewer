package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.MovieAdapter;
import io.github.javiewer.network.converter.HomePageConverter;
import io.github.javiewer.network.wrapper.MovieWrapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class MovieFragment extends Fragment {

    @Bind(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    public MovieAdapter mAdapter;
    public LinearLayoutManager mLayoutManager;

    public List<MovieWrapper> movies = new ArrayList<>();

    public SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    public MovieFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(mLayoutManager = new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter = new MovieAdapter(movies));

        mRecyclerView.addOnScrollListener(new EndlessOnScrollListener(mLayoutManager) {
            @Override
            public void onLoad() {
                Call<ResponseBody> call = getCall(page);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            List<MovieWrapper> wrappers = HomePageConverter.convert(response.body().string());
                            movies.addAll(wrappers);
                            mAdapter.notifyDataSetChanged();
                        } catch (Throwable e) {
                            onFailure(call, e);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        page--;
                    }
                });
            }
        });

        mRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this.getContext(), R.color.googleBlue),
                ContextCompat.getColor(this.getContext(), R.color.googleGreen),
                ContextCompat.getColor(this.getContext(), R.color.googleRed),
                ContextCompat.getColor(this.getContext(), R.color.googleYellow)
        );

        mRefreshLayout.setOnRefreshListener(mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Call<ResponseBody> call = getCall(1);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            List<MovieWrapper> wrappers = HomePageConverter.convert(response.body().string());
                            movies.clear();
                            movies.addAll(wrappers);
                            mAdapter.notifyDataSetChanged();
                        } catch (Throwable e) {
                            onFailure(call, e);
                        }

                        mRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
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

        private boolean loading = true;

        private int total = 0;
        private int loadThreshold = 5;
        public int page = 1;

        public EndlessOnScrollListener(LinearLayoutManager mLayoutManager) {
            this.mLayoutManager = mLayoutManager;
        }

        public abstract void onLoad();

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > total) {
                    loading = false;
                    total = totalItemCount;
                }
            }

            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + loadThreshold)) {

                page++;
                onLoad();

                loading = true;
            }
        }
    }
}
