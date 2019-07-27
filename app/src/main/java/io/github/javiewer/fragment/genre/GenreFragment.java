package io.github.javiewer.fragment.genre;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.adapter.GenreAdapter;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.view.ViewUtil;
import io.github.javiewer.view.decoration.GridSpacingItemDecoration;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class GenreFragment extends Fragment {

    @BindView(R.id.genre_recycler_view)
    public RecyclerView mRecyclerView;
    protected List<Genre> genres = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;
    private StaggeredGridLayoutManager mLayoutManager;

    public GenreFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_genre_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView.setLayoutManager(mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(this.mAdapter = new GenreAdapter(genres, this.getActivity()));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, ViewUtil.dpToPx(8), true));

        RecyclerView.ItemAnimator animator = new SlideInUpAnimator();
        animator.setAddDuration(300);
        mRecyclerView.setItemAnimator(animator);

        this.mAdapter.notifyItemRangeInserted(0, this.mAdapter.getItemCount());
    }

    public Call<ResponseBody> getCall(int page) {
        return JAViewer.SERVICE.getActresses(page);
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }
}
