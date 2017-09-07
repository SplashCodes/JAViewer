package io.github.javiewer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.view.SquareTopCrop;
import io.github.javiewer.view.listener.ActressClickListener;
import io.github.javiewer.view.listener.ActressLongClickListener;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.SOURCE;

/**
 * Project: JAViewer
 */
public class ActressAdapter extends ItemAdapter<Actress, ActressAdapter.ViewHolder> {

    private Activity mParentActivity;

    public ActressAdapter(List<Actress> actresses, Activity mParentActivity) {
        super(actresses);
        this.mParentActivity = mParentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_actress, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Actress actress = getItems().get(position);

        holder.parse(actress);

        holder.mLayout.setOnClickListener(new ActressClickListener(actress, mParentActivity));
        holder.mLayout.setOnLongClickListener(new ActressLongClickListener(actress, mParentActivity));

        holder.mImage.setImageDrawable(null);
        Glide.with(holder.mImage.getContext().getApplicationContext())
                .load(actress.getImageUrl())
                .placeholder(R.drawable.ic_movie_actresses)
                .diskCacheStrategy(SOURCE) // override default RESULT cache and apply transform always
                .skipMemoryCache(true) // do not reuse the transformed result while running
                .transform(new SquareTopCrop(holder.mImage.getContext()))
                .dontAnimate()
                .into(holder.mImage);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.actress_name)
        public TextView mTextName;

        @BindView(R.id.actress_img)
        public ImageView mImage;

        @BindView(R.id.layout_actress)
        public View mLayout;

        public void parse(Actress actress) {
            mTextName.setText(actress.getName());
            mTextName.setSelected(true);
        }

        public ViewHolder(View view) {
            super(view);

            ButterKnife.bind(this, view);
        }
    }
}
