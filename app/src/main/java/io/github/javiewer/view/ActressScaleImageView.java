package io.github.javiewer.view;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import io.github.javiewer.R;

/**
 * Project: JAViewer
 */
public class ActressScaleImageView extends ScaleImageView {

    public ActressScaleImageView(Context context) {
        super(context);
    }

    public ActressScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActressScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getDrawable() == null) {
            setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_default_actresses, null));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}