package io.github.javiewer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Project: JAViewer
 */
public class SquareImageView extends ImageView {

    public SquareImageView(Context context) {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

            setMeasuredDimension(measuredWidth, measuredWidth);

        } catch (Exception e) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}