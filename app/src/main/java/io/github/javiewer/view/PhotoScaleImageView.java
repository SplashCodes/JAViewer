package io.github.javiewer.view;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import io.github.javiewer.R;

/**
 * Project: JAViewer
 */
public class PhotoScaleImageView extends ScaleImageView {

    public PhotoScaleImageView(Context context) {
        super(context);
    }

    public PhotoScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoScaleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getDrawable() == null) {
            setScaleType(ScaleType.CENTER);
            setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_default_photo, null));
        } else {
            setScaleType(ScaleType.CENTER_CROP);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}