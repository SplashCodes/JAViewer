package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.DownloadLinkAdapter;
import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.listener.BasicOnScrollListener;
import io.github.javiewer.network.provider.DownloadLinkProvider;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DownloadFragment extends RecyclerFragment<LinearLayoutManager> {

    public List<DownloadLink> links = new ArrayList<>();

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
        super.onActivityCreated(savedInstanceState);
        this.setRecyclerViewPadding(4);

        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(new ScaleInAnimationAdapter(new DownloadLinkAdapter(links, this.getActivity(), this.provider)));

        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mScrollListener.refresh();
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
            public List<DownloadLink> getItems() {
                return DownloadFragment.this.links;
            }

            @Override
            public void onResult(ResponseBody response) throws Exception {
                super.onResult(response);
                List<DownloadLink> downloads = provider.parseDownloadLinks(response.string());

                int pos = links.size();

                if (pos > 0) {
                    pos--;
                }

                if (downloads.isEmpty()) {
                    setEnd(true);
                } else {
                    links.addAll(downloads);
                    getAdapter().notifyItemChanged(pos, downloads.size());
                }
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
        return this.provider.search(this.keyword, page);
    }
}
