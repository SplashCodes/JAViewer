package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.view.ViewUtil;
import io.github.javiewer.view.listener.BasicOnScrollListener;

/**
 * Project: JAViewer
 */
public abstract class RecyclerFragment<I, LM extends RecyclerView.LayoutManager> extends Fragment {
    @BindView(R.id.recycler_view)
    protected RecyclerView mRecyclerView;

    @BindView(R.id.refresh_layout)
    protected SwipeRefreshLayout mRefreshLayout;

    /*@BindView(R.id.adView)
    protected AdView mAdView;*/

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;

    private BasicOnScrollListener mScrollListener;

    private ArrayList<I> items = new ArrayList<>();

    protected void setRecyclerViewPadding(int dp) {
        this.mRecyclerView.setPadding(
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp),
                ViewUtil.dpToPx(dp)
        );
    }

    public RecyclerFragment() {
        // Required empty public constructor
    }

    public void setLayoutManager(LM mLayoutManager) {
        this.mRecyclerView.setLayoutManager(mLayoutManager);
    }

    @SuppressWarnings("unchecked")
    public LM getLayoutManager() {
        return (LM) this.mRecyclerView.getLayoutManager();
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mRecyclerView.setAdapter(mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        if (this.mRecyclerView == null) {
            return null;
        }
        return this.mRecyclerView.getAdapter();
    }

    public ArrayList<I> getItems() {
        return items;
    }

    public void setItems(ArrayList<I> items) {
        int size = getItems().size();
        if (size > 0) {
            getItems().clear();
            getAdapter().notifyDataSetChanged();
        }

        getItems().addAll(items);
        getAdapter().notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this.getContext(), R.color.googleBlue),
                ContextCompat.getColor(this.getContext(), R.color.googleGreen),
                ContextCompat.getColor(this.getContext(), R.color.googleRed),
                ContextCompat.getColor(this.getContext(), R.color.googleYellow)
        );

        if (savedInstanceState != null) {
            this.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable("LayoutManagerState"));
            this.setItems((ArrayList<I>) savedInstanceState.getSerializable("Items"));
            if (this.getOnScrollListener() != null) {
                this.getOnScrollListener().restoreState(savedInstanceState.getBundle("ScrollListenerState"));
            }
        }
    }

    public void addOnScrollListener(BasicOnScrollListener listener) {
        mRecyclerView.addOnScrollListener(mScrollListener = listener);
    }

    public BasicOnScrollListener getOnScrollListener() {
        return mScrollListener;
    }

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mRefreshLayout.setOnRefreshListener(mRefreshListener = listener);
    }

    public SwipeRefreshLayout.OnRefreshListener getOnRefreshListener() {
        return mRefreshListener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("Items", this.getItems());
        outState.putParcelable("LayoutManagerState", getLayoutManager().onSaveInstanceState());

        if (this.getOnScrollListener() != null) {
            outState.putBundle("ScrollListenerState", getOnScrollListener().saveState());
        }

        super.onSaveInstanceState(outState);
    }
}
