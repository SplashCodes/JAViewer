package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.ActressAdapter;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.network.provider.AVMOProvider;
import io.github.javiewer.view.decoration.ActressItemDecoration;
import io.github.javiewer.view.listener.EndlessOnScrollListener;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class ActressesFragment extends RecyclerFragment<Actress, LinearLayoutManager> {

    public ActressesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        //this.setAdapter(new SlideInBottomAnimationAdapter(new ActressAdapter(getItems(), this.getActivity())));
        this.setAdapter(new ActressAdapter(getItems(), this.getActivity()));

        mRecyclerView.addItemDecoration(new ActressItemDecoration());

        /*RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);*/

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOnScrollListener().refresh();
            }
        });

        this.addOnScrollListener(new EndlessOnScrollListener<Actress>() {
            @Override
            public Call<ResponseBody> newCall(int page) {
                return ActressesFragment.this.newCall(page);
            }

            @Override
            public RecyclerView.LayoutManager getLayoutManager() {
                return ActressesFragment.this.getLayoutManager();
            }

            @Override
            public SwipeRefreshLayout getRefreshLayout() {
                return ActressesFragment.this.mRefreshLayout;
            }

            @Override
            public RecyclerView.Adapter getAdapter() {
                return ActressesFragment.this.getAdapter();
            }

            @Override
            public List<Actress> getItems() {
                return ActressesFragment.this.getItems();
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<Actress> wrappers = AVMOProvider.parseActresses(response.string());

                int pos = getItems().size();

                getItems().addAll(wrappers);
                getAdapter().notifyItemRangeInserted(pos, wrappers.size());
            }
        });

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                getOnRefreshListener().onRefresh();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    public Call<ResponseBody> newCall(int page) {
        return JAViewer.SERVICE.getActresses(page);
    }
}
