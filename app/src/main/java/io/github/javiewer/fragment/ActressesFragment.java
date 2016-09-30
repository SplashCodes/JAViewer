package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.ActressAdapter;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.listener.EndlessOnScrollListener;
import io.github.javiewer.network.AVMO;
import io.github.javiewer.network.provider.AVMOProvider;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActressesFragment extends RecyclerFragment<StaggeredGridLayoutManager> {

    public List<Actress> actresses = new ArrayList<>();

    public ActressesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setRecyclerViewPadding(4);

        this.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        this.setAdapter(new ScaleInAnimationAdapter(new ActressAdapter(actresses, this.getActivity())));

        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.refresh();
            }
        });

        this.addOnScrollListener(new EndlessOnScrollListener<Actress>(getLayoutManager(), mRefreshLayout, this.actresses) {
            @Override
            public Call<ResponseBody> newCall(int page) {
                return ActressesFragment.this.newCall(page);
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<Actress> wrappers = AVMOProvider.parseActresses(response.string());

                int pos = actresses.size();

                if (pos > 0) {
                    pos--;
                }

                actresses.addAll(wrappers);
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

    public Call<ResponseBody> newCall(int page) {
        return AVMO.INSTANCE.getActresses(page);
    }
}
