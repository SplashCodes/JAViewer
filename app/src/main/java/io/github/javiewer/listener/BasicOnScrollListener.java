package io.github.javiewer.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Project: JAViewer
 */

public abstract class BasicOnScrollListener<I> extends RecyclerView.OnScrollListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private SwipeRefreshLayout mRefreshLayout;

    private boolean loading = false;

    private int loadThreshold = 5;
    private int currentPage = 0;

    private long token;
    private boolean end = false;

    private List<I> items;

    public BasicOnScrollListener(RecyclerView.LayoutManager mLayoutManager, SwipeRefreshLayout mRefreshLayout, List<I> items) {
        this.mLayoutManager = mLayoutManager;
        this.mRefreshLayout = mRefreshLayout;
        this.items = items;
    }

    public void reset() {
        loading = false;
        loadThreshold = 5;
        currentPage = 0;
        items.clear();
    }

    public List<I> getItems() {
        return items;
    }

    public abstract Call<ResponseBody> newCall(int page);

    public void refresh() {
        setLoading(true);
        reset();
        items.clear();
        onLoad(token = System.currentTimeMillis());
    }

    private void onLoad(final long token) {
        final int page = currentPage;
        Call<ResponseBody> call = newCall(page + 1);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (token == BasicOnScrollListener.this.token && page == currentPage) {
                    try {
                        onResult(response.body());
                        currentPage++;
                    } catch (Throwable e) {
                        onFailure(call, e);
                    }
                }

                setLoading(false);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setLoading(false);
                mRefreshLayout.setRefreshing(false);
                onExceptionCaught(t);
            }
        });
    }

    public void onExceptionCaught(Throwable t) {

    }

    public void onResult(ResponseBody response) throws Exception {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        if (!isLoading() && canLoadMore(recyclerView)) {
            onLoad(token = System.currentTimeMillis());
            loading = true;
        }
    }

    public boolean canLoadMore(RecyclerView recyclerView) {
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLayoutManager.getItemCount();

        int firstVisibleItem = 0;
        if (mLayoutManager instanceof StaggeredGridLayoutManager) {
            firstVisibleItem = ((StaggeredGridLayoutManager) mLayoutManager).findFirstVisibleItemPositions(null)[0];
        } else if (mLayoutManager instanceof GridLayoutManager) {
            firstVisibleItem = ((GridLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        } else if (mLayoutManager instanceof LinearLayoutManager) {
            firstVisibleItem = ((LinearLayoutManager) mLayoutManager).findFirstVisibleItemPosition();
        }

        return (totalItemCount - visibleItemCount) <= (firstVisibleItem + this.loadThreshold);
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public boolean isEnd() {
        return end;
    }
}
