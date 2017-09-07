package io.github.javiewer.fragment.favourite;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.github.javiewer.adapter.ItemAdapter;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.fragment.RecyclerFragment;

/**
 * Project: JAViewer
 */

public abstract class FavouriteFragment extends RecyclerFragment<Movie, LinearLayoutManager> {

    public void update() {
        RecyclerView.Adapter adapter = getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.setLayoutManager(new LinearLayoutManager(this.getContext()));
        this.setAdapter(adapter());
        //this.setAdapter(adapter =
        mRefreshLayout.setEnabled(false);

        if (decoration() != null) {
            mRecyclerView.addItemDecoration(decoration());
        }

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public abstract ItemAdapter adapter();

    public RecyclerView.ItemDecoration decoration() {
        return null;
    }
}
