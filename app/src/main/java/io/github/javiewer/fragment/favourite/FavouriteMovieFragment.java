package io.github.javiewer.fragment.favourite;

import android.support.v7.widget.RecyclerView;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.ItemAdapter;
import io.github.javiewer.adapter.MovieAdapter;
import io.github.javiewer.view.decoration.MovieItemDecoration;

/**
 * Project: JAViewer
 */

public class FavouriteMovieFragment extends FavouriteFragment {
    @Override
    public ItemAdapter adapter() {
        return new MovieAdapter(JAViewer.CONFIGURATIONS.getStarredMovies(), this.getActivity());
    }

    @Override
    public RecyclerView.ItemDecoration decoration() {
        return new MovieItemDecoration();
    }
}
