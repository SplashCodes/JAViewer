package android.support.design.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Project: JAViewer
 */
public class CompactCollapsingToolbarLayout extends CollapsingToolbarLayout {
    public CompactCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CompactCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int mode = MeasureSpec.getMode(heightMeasureSpec);
        final int topInset = mLastInsets != null ? mLastInsets.getSystemWindowInsetTop() : 0;
        if (mode == MeasureSpec.UNSPECIFIED && topInset > 0) {
            // fix the bottom empty padding
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    getMeasuredHeight() - topInset, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
