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
        int size = this.getItems().size();
        if (size > 0) {
            this.getItems().clear();
            notifyItemRangeRemoved(0, size);
        }
        this.getItems().addAll(items);
        notifyItemRangeInserted(0, items.size());
    }

    @Override
    public int getItemCount() {
        return getItems().size();
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        holder.itemView.clearAnimation();
        super.onViewDetachedFromWindow(holder);
    }
}
