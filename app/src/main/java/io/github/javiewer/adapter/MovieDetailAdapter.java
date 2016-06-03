package io.github.javiewer.adapter;

import android.app.Activity;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.MainActivity;
import io.github.javiewer.network.wrapper.MovieDetailWrapper;
import io.github.javiewer.network.wrapper.ScreenshotWrapper;

/**
 * Project: JAViewer
 */
public class MovieDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScreenshotWrapper> screenshots;

    private Activity mParentActivity;

    private ScreenshotAdapter mScreenshotAdapter;

    private MovieDetailWrapper detailInfo = null;

    public MovieDetailAdapter(List<ScreenshotWrapper> screenshots, Activity mParentActivity) {
        this.screenshots = screenshots;
        this.mParentActivity = mParentActivity;
    }

    public void onInit(MovieDetailWrapper detailInfo) {
        this.detailInfo = detailInfo;
        notifyItemRangeChanged(0, this.getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_info, parent, false);

                return new InfoViewHolder(v);
            }

            case 1: {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_screenshots, parent, false);

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
        switch (position) {
            //基本信息
            case 0: {
                InfoViewHolder vh = (InfoViewHolder) holder;
                vh.mCodeText.setText(detailInfo.code);
                vh.mDateText.setText(detailInfo.date);
                vh.mDurationText.setText(detailInfo.duration);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (detailInfo == null ? 0 : 1) + //基本信息
                (screenshots == null || screenshots.isEmpty() ? 0 : 1); //截图
    }

    public class InfoViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.info_text_code)
        TextView mCodeText;

        @Bind(R.id.info_text_date)
        TextView mDateText;

        @Bind(R.id.info_text_duration)
        TextView mDurationText;

        public InfoViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }

    public class ScreenshotsViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.screenshots_recycler_view)
        RecyclerView mRecyclerView;

        @Bind(R.id.movie_icon_photo)
        ImageView mIcon;

        public ScreenshotsViewHolder(View view, List<ScreenshotWrapper> screenshots, Activity mParentActivity) {
            super(view);

            ButterKnife.bind(this, view);

            mRecyclerView.setAdapter(mScreenshotAdapter = new ScreenshotAdapter(screenshots, mParentActivity, mIcon));
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
            mRecyclerView.setNestedScrollingEnabled(false);
        }
    }

    public class ScreenshotAdapter extends RecyclerView.Adapter<ScreenshotAdapter.ViewHolder> {

        private List<ScreenshotWrapper> screenshots;

        private Activity mParentActivity;

        private ImageView mIcon;

        public ScreenshotAdapter(List<ScreenshotWrapper> screenshots, Activity mParentActivity, ImageView mIcon) {
            this.screenshots = screenshots;
            this.mParentActivity = mParentActivity;
            this.mIcon = mIcon;
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

            if (position == 0) {
                holder.mImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        mIcon.setPadding(
                                mIcon.getPaddingLeft(),
                                (holder.mImage.getMeasuredHeight() - mIcon.getMeasuredHeight()) / 2,
                                mIcon.getPaddingRight(),
                                mIcon.getPaddingBottom()
                        );

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            holder.mImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        } else {
                            holder.mImage.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        }
                    }
                });
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
}
