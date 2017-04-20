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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.view.SquareTopCrop;
import io.github.javiewer.view.ViewUtil;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

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


        holder.mName.setText(actress.getName());

        if (position == 0) {
            ViewUtil.alignIconToView(mIcon, holder.mImage);
        }

        holder.mImage.setImageResource(R.drawable.ic_movie_actresses);

        if (actress.getImageUrl().trim().isEmpty()) {
            return;
        }

        Glide.with(holder.mImage.getContext())
                .load(actress.getImageUrl())
                .asBitmap()
                .placeholder(R.drawable.ic_movie_actresses)
                .diskCacheStrategy(SOURCE) // override default RESULT cache and apply transform always
                .skipMemoryCache(true) // do not reuse the transformed result while running
                .transform(new SquareTopCrop(holder.mImage.getContext()))
                //.transform(new PositionedCropTransformation(holder.mImage.getContext(), 0, 0))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        //resource = Bitmap.createBitmap(resource, 0, 0, resource.getWidth(), resource.getWidth());
                        holder.mImage.setImageBitmap(resource);

                        try {
                            Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
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
                        } catch (Exception ignored) {
                        }
                    }
                });

    }

    @Override
    public int getItemCount() {
        return actresses == null ? 0 : actresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.actress_palette_img)
        public ImageView mImage;

        @BindView(R.id.actress_palette_name)
        public TextView mName;

        @BindView(R.id.card_actress_palette)
        public CardView mCard;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
