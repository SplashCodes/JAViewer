package io.github.javiewer.fragment.favourite;

import android.support.v7.widget.RecyclerView;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.ActressAdapter;
import io.github.javiewer.adapter.ItemAdapter;
import io.github.javiewer.view.decoration.ActressItemDecoration;

/**
 * Project: JAViewer
 */

public class FavouriteActressFragment extends FavouriteFragment {
    @Override
    public ItemAdapter adapter() {
        return new ActressAdapter(JAViewer.CONFIGURATIONS.getStarredActresses(), this.getActivity());
    }

    @Override
    public RecyclerView.ItemDecoration decoration() {
        return new ActressItemDecoration();
    }
}
