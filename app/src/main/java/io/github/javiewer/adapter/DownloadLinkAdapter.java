package io.github.javiewer.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.DownloadLink;

/**
 * Project: JAViewer
 */
public class DownloadLinkAdapter extends RecyclerView.Adapter<DownloadLinkAdapter.ViewHolder> {

    private List<DownloadLink> links;

    private Activity mParentActivity;

    public DownloadLinkAdapter(List<DownloadLink> links, Activity mParentActivity) {
        this.links = links;
        this.mParentActivity = mParentActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_download, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final DownloadLink link = links.get(position);

        holder.parse(link);

        //TODO:
        /*holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mParentActivity, MovieActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("title", link.title);
                bundle.putString("detail", link.detailUrl);
                intent.putExtras(bundle);

                mParentActivity.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return links == null ? 0 : links.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.download_title)
        public TextView mTextTitle;

        @Bind(R.id.download_size)
        public TextView mTextSize;

        @Bind(R.id.download_date)
        public TextView mTextDate;

        public void parse(DownloadLink link) {
            mTextSize.setText(link.getSize());
            mTextTitle.setText(link.getTitle());
            mTextDate.setText(link.getDate());
        }

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
