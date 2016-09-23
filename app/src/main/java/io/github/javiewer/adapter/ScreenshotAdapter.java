package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.GalleryActivity;
import io.github.javiewer.activity.MainActivity;
import io.github.javiewer.adapter.item.Screenshot;
import io.github.javiewer.view.ViewUtil;

/**
 * Created by MagicDroidX on 2016/9/23.
 */

public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

    private List<Screenshot> screenshots;

    private Activity mParentActivity;

    private ImageView mIcon;

    public ScreenshotAdapter(List<Screenshot> screenshots, Activity mParentActivity, ImageView mIcon) {
        this.screenshots = screenshots;
        this.mParentActivity = mParentActivity;
        this.mIcon = mIcon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_screenshot, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Screenshot screenshot = screenshots.get(position);

        ImageLoader.getInstance().displayImage(screenshot.getThumbnailUrl(), holder.mImage, MainActivity.displayImageOptions);

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

        @Bind(R.id.screenshot_image_view)
        public ImageView mImage;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
