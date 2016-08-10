package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.Genre;

/**
 * Project: JAViewer
 */
public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.ViewHolder> {

    private List<Genre> genres;

    private Activity mParentActivity;

    public GenreAdapter(List<Genre> genres, Activity mParentActivity) {
        this.genres = genres;
        this.mParentActivity = mParentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_genre, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Genre genre = genres.get(position);
        holder.parse(genre);

        holder.mTextName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genre.getLink() != null) {
                    Intent intent = new Intent(mParentActivity, MovieListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", genre.getTitle());
                    bundle.putString("query", genre.getLink());

                    intent.putExtras(bundle);

                    mParentActivity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres == null ? 0 : genres.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.genre_name)
        public TextView mTextName;

        public void parse(Genre genre) {
            mTextName.setText(genre.getTitle());
        }

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
