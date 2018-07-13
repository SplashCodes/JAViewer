package io.github.javiewer.util;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZVideoPlayerStandard;
import io.github.javiewer.R;

/**
 * Project: JAViewer
 */
public class SimpleVideoPlayer extends JZVideoPlayerStandard {
    public SimpleVideoPlayer(Context context) {
        this(context, null);
    }

    public SimpleVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_video_player;
    }
}
