package io.github.javiewer.view;

import android.content.res.Resources;
import android.graphics.Matrix;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Project: JAViewer
 */

public class ViewUtil {
    @SuppressWarnings("deprecation")
    public static void alignIconToView(final View icon, final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                icon.setPadding(
                        icon.getPaddingLeft(),
                        (view.getMeasuredHeight() - icon.getMeasuredHeight()) / 2,
                        icon.getPaddingRight(),
                        icon.getPaddingBottom()
                );

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static void imageTopCrop(ImageView view) {
        view.setScaleType(ImageView.ScaleType.MATRIX);
        final Matrix matrix = view.getImageMatrix();

        float scale;
        final int viewWidth = view.getWidth() - view.getPaddingLeft() - view.getPaddingRight();
        final int viewHeight = view.getHeight() - view.getPaddingTop() - view.getPaddingBottom();
        final int drawableWidth = view.getDrawable().getIntrinsicWidth();
        final int drawableHeight = view.getDrawable().getIntrinsicHeight();

        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }

        matrix.setScale(scale, scale);
        view.setImageMatrix(matrix);
    }
}
