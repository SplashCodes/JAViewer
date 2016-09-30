package io.github.javiewer.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.Actress;

/**
 * Project: JAViewer
 */
public class ActressAdapter extends RecyclerView.Adapter<ActressAdapter.ViewHolder> {

    private List<Actress> actresses;

    private Activity mParentActivity;

    public ActressAdapter(List<Actress> actresses, Activity mParentActivity) {
        this.actresses = actresses;
        this.mParentActivity = mParentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_actress, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Actress actress = actresses.get(position);

        holder.parse(actress);

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


        ImageLoader.getInstance().displayImage(actress.getImageUrl(), holder.mImage, JAViewer.DISPLAY_IMAGE_OPTIONS);
    }

    @Override
    public int getItemCount() {
        return actresses == null ? 0 : actresses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.actress_name)
        public TextView mTextName;

        @BindView(R.id.actress_img)
        public ImageView mImage;

        @BindView(R.id.card_actress)
        public CardView mCard;

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
