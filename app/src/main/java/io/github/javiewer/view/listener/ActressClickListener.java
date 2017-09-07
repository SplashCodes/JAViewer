package io.github.javiewer.view.listener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.github.javiewer.activity.MovieListActivity;
import io.github.javiewer.adapter.item.Actress;

/**
 * Project: JAViewer
 */

public class ActressClickListener implements View.OnClickListener {

    private Activity mActivity;
    private Actress actress;

    public ActressClickListener(Actress actress, Activity mActivity) {
        this.actress = actress;
        this.mActivity = mActivity;
    }

    @Override
    public void onClick(View v) {
        if (actress.getLink() != null) {
            Intent intent = new Intent(mActivity, MovieListActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("title", actress.getName() + " 的作品");
            bundle.putString("link", actress.getLink());

            intent.putExtras(bundle);

            mActivity.startActivity(intent);
        }
    }
}
