package io.github.javiewer.listener;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Project: JAViewer
 */

public abstract class EndlessOnScrollListener<I> extends BasicOnScrollListener<I> {

    public EndlessOnScrollListener(RecyclerView.LayoutManager mLayoutManager, SwipeRefreshLayout mRefreshLayout, List<I> items) {
        super(mLayoutManager, mRefreshLayout, items);
    }

    @Override
    public boolean isEnd() {
        return false;
    }
}
