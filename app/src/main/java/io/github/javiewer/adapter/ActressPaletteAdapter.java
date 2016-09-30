package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.view.ViewUtil;

/**
 * Project: JAViewer
 */

public class ActressPaletteAdapter extends RecyclerView.Adapter<ActressPaletteAdapter.ViewHolder> {

    private List<Actress> actresses;

    private Activity mParentActivity;

    private ImageView mIcon;

    public ActressPaletteAdapter(List<Actress> actresses, Activity mParentActivity, ImageView mIcon) {
        this.actresses = actresses;
        this.mParentActivity = mParentActivity;
        this.mIcon = mIcon;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_actress_palette, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Actress actress = actresses.get(position);

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actress.getLink() != null) {
                    Intent intent = new Intent(mParentActivity, MovieListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", actress.getName() + " 的作品");
                    bundle.putString("link", actress.getLink());

                    intent.putExtras(bundle);

                    mParentActivity.startActivity(intent);
                }
            }
        });

        ImageLoader.getInstance().displayImage(actress.getImageUrl(), holder.mImage, JAViewer.DISPLAY_IMAGE_OPTIONS, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);

                Palette.from(loadedImage).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        Palette.Swatch swatch = palette.getLightVibrantSwatch();
                        if (swatch == null) {
                            return;
                        }
                        holder.mCard.setCardBackgroundColor(swatch.getRgb());
                        holder.mName.setTextColor(swatch.getBodyTextColor());
                    }
                });
            }
        });

        holder.mName.setText(actress.getName());

        if (position == 0) {
            ViewUtil.alignIconToView(mIcon, holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        return actresses == null ? 0 : actresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.actress_palette_img)
        public ImageView mImage;

        @Bind(R.id.actress_palette_name)
        public TextView mName;

        @Bind(R.id.card_actress_palette)
        public CardView mCard;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
