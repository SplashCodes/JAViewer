package io.github.javiewer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.MainActivity;
import io.github.javiewer.network.wrapper.ScreenshotWrapper;

/**
 * Project: JAViewer
 */
public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScreenshotWrapper> screenshots;

    private Activity mParentActivity;

    private ScreenshotAdapter mScreenshotAdapter;

    public MovieDetailAdapter(List<ScreenshotWrapper> screenshots, Activity mParentActivity) {
        this.screenshots = screenshots;
        this.mParentActivity = mParentActivity;
    }

    public void onUpdate() {
        notifyDataSetChanged();
        mScreenshotAdapter.notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.screenshots_recycler, parent, false);

                return new ScreenshotsViewHolder(v, screenshots, mParentActivity);
            }
        }

        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // TODO: 2016/5/24
    }

    @Override
    public int getItemCount() {
        return (screenshots == null ? 0 : 1)
                + 0;
    }

    public class ScreenshotsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.screenshots_recycler_view)
        RecyclerView mRecyclerView;

        public ScreenshotsViewHolder(View view, List<ScreenshotWrapper> screenshots, Activity mParentActivity) {
            super(view);

            ButterKnife.bind(this, view);

            mRecyclerView.setAdapter(mScreenshotAdapter = new ScreenshotAdapter(screenshots, mParentActivity));
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

        private List<ScreenshotWrapper> screenshots;

        private Activity mParentActivity;

        public ScreenshotAdapter(List<ScreenshotWrapper> screenshots, Activity mParentActivity) {
            this.screenshots = screenshots;
            this.mParentActivity = mParentActivity;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_screenshot, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ScreenshotWrapper wrapper = screenshots.get(position);

            ImageLoader.getInstance().displayImage(wrapper.thumbnailUrl, holder.mImage, MainActivity.displayImageOptions);
            //TODO: 加载大图
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
}
