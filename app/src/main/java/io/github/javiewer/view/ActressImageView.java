package io.github.javiewer.view;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import io.github.javiewer.R;

/**
 * Project: JAViewer
 */
public class ActressImageView extends ImageView {

    public ActressImageView(Context context) {
        super(context);
    }

    public ActressImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActressImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getDrawable() == null) {
            setImageDrawable(ResourcesCompat.getDrawable(this.getResources(), R.drawable.ic_movie_actresses, null));
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}