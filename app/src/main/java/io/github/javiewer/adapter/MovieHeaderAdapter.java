package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.view.ViewUtil;

/**
 * Project: JAViewer
 */

public class MovieHeaderAdapter extends RecyclerView.Adapter<MovieHeaderAdapter.ViewHolder> {

    private List<MovieDetail.Header> headers;

    private Activity mParentActivity;

    private ImageView mIcon;

    public boolean first = true;

    public MovieHeaderAdapter(List<MovieDetail.Header> headers, Activity mParentActivity, ImageView mIcon) {
        this.headers = headers;
        this.mParentActivity = mParentActivity;
        this.mIcon = mIcon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_header, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MovieDetail.Header header = headers.get(position);

        if (header.name != null && header.value != null) {
            holder.mHeaderName.setText(header.name);
            holder.mHeaderValue.setText(header.value);

            if (header.getLink() != null) {
                holder.mHeaderValue.setPaintFlags(holder.mHeaderValue.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                holder.mHeaderValue.setTextColor(ResourcesCompat.getColor(this.mParentActivity.getResources(), R.color.colorAccent, null));
                holder.mHeaderValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mParentActivity, MovieListActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("title", header.getName() + " " + header.getValue());
                        bundle.putString("link", header.getLink());
                        intent.putExtras(bundle);
                        mParentActivity.startActivity(intent);
                    }
                });
            }

            if (first) {
                ViewUtil.alignIconToView(mIcon, holder.mHeaderName);
                first = false;
            }
        }
    }

    @Override
    public int getItemCount() {
        return headers == null ? 0 : headers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.header_name)
        public TextView mHeaderName;

        @Bind(R.id.header_value)
        public TextView mHeaderValue;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
