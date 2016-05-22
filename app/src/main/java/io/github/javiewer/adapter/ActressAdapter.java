package io.github.javiewer.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.network.wrapper.ActressWrapper;

/**
 * Project: JAViewer
 */
public class ActressAdapter extends RecyclerView.Adapter<ActressAdapter.ViewHolder> {

    private static DisplayImageOptions options = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading()
            .cacheInMemory()
            .cacheOnDisc()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .delayBeforeLoading(1000)
            .displayer(new FadeInBitmapDisplayer(500)) // default
            .build();

    private List<ActressWrapper> actresses;

    public ActressAdapter(List<ActressWrapper> actresses) {
        this.actresses = actresses;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_actress, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
       holder.mImage.setMinimumHeight( holder.mImage.getWidth());
        ActressWrapper actress = actresses.get(position);

        holder.parse(actress);

        /*ImageLoader.getInstance().loadImage(actress.imageUrl, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.mImage.setImageBitmap(loadedImage);
            }
        });*/

        ImageLoader.getInstance().displayImage(actress.imageUrl, holder.mImage, options);
    }

    @Override
    public int getItemCount() {
        return actresses == null ? 0 : actresses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.actress_name)
        public TextView mTextName;

        @Bind(R.id.actress_img)
        public ImageView mImage;

        public void parse(ActressWrapper actress) {
            mTextName.setText(actress.name);
        }

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
