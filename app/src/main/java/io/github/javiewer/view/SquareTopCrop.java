package io.github.javiewer.view;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Project: JAViewer
 */

public class SquareTopCrop extends BitmapTransformation {

    public SquareTopCrop(Context context) {
        super(context);
    }

    // Bitmap doesn't implement equals, so == and .equals are equivalent here.
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getWidth());
    }

    @Override
    public String getId() {
        return "SquareTopCrop";
    }
}
