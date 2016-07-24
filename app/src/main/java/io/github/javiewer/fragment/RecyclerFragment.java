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

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;

/**
 * Project: JAViewer
 */
public abstract class RecyclerFragment<LM extends RecyclerView.LayoutManager> extends Fragment {
    @Bind(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    private RecyclerView.Adapter mAdapter;
    private LM mLayoutManager;

    public RecyclerFragment() {
        // Required empty public constructor
    }

    public void setLayoutManager(LM mLayoutManager) {
        this.mRecyclerView.setLayoutManager(this.mLayoutManager = mLayoutManager);
    }

    public LM getLayoutManager() {
        return mLayoutManager;
    }

    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mRecyclerView.setAdapter(this.mAdapter = mAdapter);
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this.getContext(), R.color.googleBlue),
                ContextCompat.getColor(this.getContext(), R.color.googleGreen),
                ContextCompat.getColor(this.getContext(), R.color.googleRed),
                ContextCompat.getColor(this.getContext(), R.color.googleYellow)
        );
    }
}
