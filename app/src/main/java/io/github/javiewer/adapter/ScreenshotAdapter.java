package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.GalleryActivity;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.adapter.item.Screenshot;
import io.github.javiewer.view.ViewUtil;

/**
 * Project: JAViewer
 */

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

    private List<Screenshot> screenshots;

    private Activity mParentActivity;

    private ImageView mIcon;

    private Movie movie;

    public ScreenshotAdapter(List<Screenshot> screenshots, Activity mParentActivity, ImageView mIcon, Movie movie) {
        this.screenshots = screenshots;
        this.mParentActivity = mParentActivity;
        this.mIcon = mIcon;
        this.movie = movie;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_screenshot, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Screenshot screenshot = screenshots.get(position);

        holder.mImage.setImageDrawable(null);
        Glide.with(holder.mImage.getContext().getApplicationContext())
                .load(screenshot.getThumbnailUrl())
                .into(holder.mImage);

        holder.mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mParentActivity, GalleryActivity.class);
                Bundle bundle = new Bundle();

                String[] urls = new String[screenshots.size()];
                for (int k = 0; k < screenshots.size(); k++) {
                    urls[k] = screenshots.get(k).getImageUrl();
                }
                bundle.putStringArray("urls", urls);
                bundle.putSerializable("movie", movie);
                bundle.putInt("position", holder.getAdapterPosition());
                i.putExtras(bundle);
                mParentActivity.startActivity(i);

            }
        });

        if (position == 0) {
            ViewUtil.alignIconToView(mIcon, holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        return screenshots == null ? 0 : screenshots.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.screenshot_image_view)
        public ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
