package io.github.javiewer.adapter;

import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * Project: JAViewer
 */

public abstract class ItemAdapter<I, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    private List<I> items;

    public ItemAdapter(List<I> items) {
        this.items = items;
    }

    public List<I> getItems() {
        return items;
    }

    public void setItems(List<I> items) {
        int startPosition = 0;
        int preSize = this.getItems().size();
        if (preSize > 0) {
            this.getItems().clear();
            notifyItemRangeRemoved(startPosition, preSize);
        }
        this.getItems().addAll(items);
        notifyItemRangeChanged(startPosition, items.size());
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }
}
