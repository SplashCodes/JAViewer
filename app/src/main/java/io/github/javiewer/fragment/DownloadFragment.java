package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.DownloadLinkAdapter;
import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.network.provider.DownloadLinkProvider;
import io.github.javiewer.view.decoration.DownloadItemDecoration;
import io.github.javiewer.view.listener.BasicOnScrollListener;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadFragment extends RecyclerFragment<DownloadLink, LinearLayoutManager> {

    public DownloadLinkProvider provider;

    public String keyword;

    public DownloadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        this.provider = DownloadLinkProvider.getProvider(bundle.getString("provider"));
        this.keyword = bundle.getString("keyword");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        /*if (JAViewer.CONFIGURATIONS.showAds()) {
            mAdView.setVisibility(View.VISIBLE);
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("52546C5153814CA9A9714647F5960AFE")
                    .build();
            mAdView.loadAd(adRequest);
        }*/

        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(new ScaleInAnimationAdapter(new DownloadLinkAdapter(this.getItems(), this.getActivity(), this.provider)));
        mRecyclerView.addItemDecoration(new DownloadItemDecoration());

        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOnScrollListener().refresh();
            }
        });

        this.addOnScrollListener(new BasicOnScrollListener<DownloadLink>() {
            @Override
            public Call<ResponseBody> newCall(int page) {
                return DownloadFragment.this.newCall(page);
            }

            @Override
            public RecyclerView.LayoutManager getLayoutManager() {
                return DownloadFragment.this.getLayoutManager();
            }

            @Override
            public SwipeRefreshLayout getRefreshLayout() {
                return DownloadFragment.this.mRefreshLayout;
            }

            @Override
            public RecyclerView.Adapter getAdapter() {
                return DownloadFragment.this.getAdapter();
            }

            @Override
            public List<DownloadLink> getItems() {
                return DownloadFragment.this.getItems();
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<DownloadLink> downloads = provider.parseDownloadLinks(response.string());

                int pos = getItems().size();

                if (downloads.isEmpty()) {
                    setEnd(true);
                } else {
                    getItems().addAll(downloads);
                    getAdapter().notifyItemRangeInserted(pos, downloads.size());
                }
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
        return this.provider.search(this.keyword, page);
    }
}
