package io.github.javiewer.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.javiewer.JAViewer;
import io.github.javiewer.R;
import io.github.javiewer.adapter.item.Movie;

public class GalleryActivity extends AppCompatActivity {

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;

    Animation fadeIn = new AlphaAnimation(0, 1);

    {
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(150);
    }

    Animation fadeOut = new AlphaAnimation(1, 0);

    {
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(150);
    }

    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mToolbar.startAnimation(fadeIn);

            //mControlsView.setVisibility(View.VISIBLE);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    GestureDetector detector;

    @BindView(R.id.gallery_pager)
    public ViewPager mPager;

    @BindView(R.id.toolbar_gallery)
    public Toolbar mToolbar;

    private String[] imageUrls;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gallery);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                toggle();
                return true;
            }
        });
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                detector.onTouchEvent(event);
                return false;
            }
        });

        {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            mToolbar.startAnimation(fadeOut);
            //mControlsView.setVisibility(View.GONE);
            mVisible = false;
            mHidePart2Runnable.run();
        }

        Bundle bundle = this.getIntent().getExtras();

        mPager.setAdapter(new ImageAdapter(this, imageUrls = bundle.getStringArray("urls")));
        mPager.setCurrentItem(bundle.getInt("position"));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                updateIndicator();
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        updateIndicator();

        movie = (Movie) bundle.getSerializable("movie");
    }

    private void updateIndicator() {
        delayedHide(AUTO_HIDE_DELAY_MILLIS);
        getSupportActionBar().setTitle((mPager.getCurrentItem() + 1) + " / " + (imageUrls.length));
        //mTextIndicator.setText((mPager.getCurrentItem() + 1) + " / " + (imageUrls.length));
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
        }
    }

    private void hide() {
        hide(UI_ANIMATION_DELAY);
    }

    private void hide(int delay) {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mToolbar.startAnimation(fadeOut);
        //mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, delay);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                final File dir = new File(
                        JAViewer.getStorageDir(),
                        String.format("/movies/[%s] %s", movie.code, movie.title).replaceAll("^(?!(COM[0-9]|LPT[0-9]|CON|PRN|AUX|CLOCK\\$|NUL)$)[^./\\\\:*?\u200C\u200B\"<>|]+$", "-")
                );
                dir.mkdirs();
                final int index = mPager.getCurrentItem();
                Glide
                        .with(this)
                        .load(imageUrls[index])
                        .asBitmap()
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                try {
                                    OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(dir, (index + 1) + ".jpeg")));
                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, os);
                                    os.flush();
                                    os.close();
                                    Toast.makeText(GalleryActivity.this, "成功保存到 " + dir, Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    onLoadFailed(e, null);
                                }
                            }
                        });
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private static class ImageAdapter extends PagerAdapter {

        private final String[] imageUrls;
        private LayoutInflater inflater;
        //private DisplayImageOptions options;

        ImageAdapter(Context context, String[] imageUrls) {
            inflater = LayoutInflater.from(context);

            this.imageUrls = imageUrls;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.content_gallery, view, false);
            final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            final ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progress_bar);
            final TextView textView = (TextView) imageLayout.findViewById(R.id.gallery_text_error);

            Glide.with(imageView.getContext().getApplicationContext())
                    .load(imageUrls[position])
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            progressBar.setVisibility(View.GONE);
                            imageView.setImageDrawable(resource);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            textView.setText("图片加载失败 :(\n" + e.getMessage());
                        }
                    });

            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }
    }
}
