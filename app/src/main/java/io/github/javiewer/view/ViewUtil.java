package io.github.javiewer.view;

import android.content.res.Resources;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by MagicDroidX on 2016/9/23.
 */

public class ViewUtil {
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
}
