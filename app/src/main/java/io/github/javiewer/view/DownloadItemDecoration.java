package io.github.javiewer.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Project: JAViewer
 */

public class DownloadItemDecoration extends RecyclerView.ItemDecoration {
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        Rect rect = new Rect();
        if (parent.indexOfChild(view) == 0) {
            rect.top = ViewUtil.dpToPx(8);
        }
        outRect.set(rect);
    }
}
