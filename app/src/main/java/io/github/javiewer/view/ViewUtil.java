package io.github.javiewer.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
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

                ViewGroup.MarginLayoutParams viewMargin = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                ViewGroup.MarginLayoutParams iconMargin = (ViewGroup.MarginLayoutParams) icon.getLayoutParams();
                int topMargin = viewMargin.topMargin;
                topMargin += (view.getMeasuredHeight() - icon.getMeasuredHeight()) / 2;

                iconMargin.topMargin = topMargin;
                icon.setLayoutParams(iconMargin);

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

    public static Bitmap getBitmapByView(NestedScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
            scrollView.getChildAt(i).setBackgroundColor(
                    Color.parseColor("#ffffff"));
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static int getStatusBarHeight(Activity activity) {
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }
}
