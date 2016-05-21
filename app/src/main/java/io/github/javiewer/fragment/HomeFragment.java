package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.MovieAdapter;
import io.github.javiewer.network.Network;
import io.github.javiewer.network.converter.HomePageConverter;
import io.github.javiewer.network.wrapper.MovieWrapper;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Author: MagicDroidX
 */
public class HomeFragment extends RecyclerFragment {

    public MovieAdapter mAdapter;

    public List<MovieWrapper> movies = new ArrayList<>();

    public SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setAdapter(mAdapter = new MovieAdapter(movies));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        mRefreshLayout.setOnRefreshListener(mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://avmo.pw")
                        .build();

                Network network = retrofit.create(Network.class);

                Call<ResponseBody> call = network.getHomePage(1);
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
}
