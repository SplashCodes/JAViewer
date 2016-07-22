package io.github.javiewer.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.DownloadLinkAdapter;
import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.network.AVMO;
import io.github.javiewer.network.BTSO;
import io.github.javiewer.provider.DownloadLinkProvider;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadFragment extends RecyclerFragment<LinearLayoutManager> {

    public List<DownloadLink> links = new ArrayList<>();

    public SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    public EndlessOnScrollListener mScrollListener;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        this.provider = DownloadLinkProvider.getProvider(getArguments().getString("provider"));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(new ScaleInAnimationAdapter(new DownloadLinkAdapter(links, this.getActivity())));
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
                                List<DownloadLink> downloads = provider.parseDownloadLinks(response.body().string());

                                if (refresh) {
                                    links.clear();
                                }

                                int pos = links.size();

                                if (pos > 0) {
                                    pos--;
                                }

                                if (downloads.isEmpty()) {
                                    bottom = true;
                                } else {
                                    links.addAll(downloads);
                                    getAdapter().notifyItemChanged(pos, downloads.size());

                                    currentPage++;
                                }
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

    public Call<ResponseBody> getCall(int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BTSO.BASE_URL)

                .build();

        BTSO btso = retrofit.create(BTSO.class);

        return btso.search(this.keyword, page);
    }

    public static abstract class EndlessOnScrollListener extends RecyclerView.OnScrollListener {

        public LinearLayoutManager mLayoutManager;

        public boolean loading = false;
        boolean bottom = false;

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

            if (!bottom && !loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + loadThreshold)) {
                onLoad(latestLoadingTime = System.currentTimeMillis(), false);
                loading = true;
            }
        }
    }
}
