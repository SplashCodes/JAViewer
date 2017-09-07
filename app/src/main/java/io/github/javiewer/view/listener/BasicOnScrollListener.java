package io.github.javiewer.view.listener;

import android.os.Bundle;
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

    private boolean loading = false;

    private int loadThreshold = 5;
    private int currentPage = 0;

    private long token;
    private boolean end = false;


    public void reset() {
        loading = false;
        loadThreshold = 5;
        currentPage = 0;
        int oldSize = getItems().size();
        if (oldSize > 0) {
            getItems().clear();
            getAdapter().notifyItemRangeRemoved(0, oldSize);
        }
    }

    public Bundle saveState() {
        Bundle bundle = new Bundle();
        bundle.putInt("CurrentPage", currentPage);
        return bundle;
    }

    public void restoreState(Bundle bundle) {
        currentPage = bundle.getInt("CurrentPage");
    }

    public abstract RecyclerView.LayoutManager getLayoutManager();

    public abstract SwipeRefreshLayout getRefreshLayout();

    public abstract List<I> getItems();

    public abstract RecyclerView.Adapter getAdapter();

    public abstract Call<ResponseBody> newCall(int page);

    public void refresh() {
        setLoading(true);
        reset();
        onLoad(token = System.currentTimeMillis());
    }

    private void onLoad(final long token) {
        final int page = currentPage;
        Call<ResponseBody> call = newCall(page + 1);

        if (call == null) {
            setLoading(false);
            getRefreshLayout().setRefreshing(false);
            return;
        }
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
                getRefreshLayout().setRefreshing(false);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                setLoading(false);
                getRefreshLayout().setRefreshing(false);
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
        RecyclerView.LayoutManager mLayoutManager = getLayoutManager();
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
