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

public abstract class RecyclerFragment extends Fragment {

    @Bind(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @Bind(R.id.refresh_layout)
    public SwipeRefreshLayout mRefreshLayout;

    public RecyclerFragment() {
        // Required empty public constructor
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
                ContextCompat.getColor(this.getContext(),R.color.googleBlue),
                ContextCompat.getColor(this.getContext(),R.color.googleGreen),
                ContextCompat.getColor(this.getContext(),R.color.googleRed),
                ContextCompat.getColor(this.getContext(),R.color.googleYellow)
        );
    }
}
