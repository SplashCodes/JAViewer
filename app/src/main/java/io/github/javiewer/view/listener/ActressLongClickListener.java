package io.github.javiewer.view.listener;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import io.github.javiewer.JAViewer;
import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.fragment.favourite.FavouriteTabsFragment;

/**
 * Project: JAViewer
 */

public class ActressLongClickListener implements View.OnLongClickListener {

    private Activity mActivity;
    private Actress actress;

    public ActressLongClickListener(Actress actress, Activity mActivity) {
        this.actress = actress;
        this.mActivity = mActivity;
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        String[] items;
        final List<Actress> actresses = JAViewer.CONFIGURATIONS.getStarredActresses();
        final boolean contain = actresses.contains(actress);
        if (contain) {
            items = new String[]{"复制女优名字", "取消收藏"};
        } else {
            items = new String[]{"复制女优名字", "收藏"};
        }
        builder.setTitle(actress.getName())
                .setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                ClipboardManager clip = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                                clip.setPrimaryClip(ClipData.newPlainText("actress", actress.getName()));
                                Toast.makeText(mActivity, "已复制到剪贴板", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            case 1: {
                                if (contain) {
                                    actresses.remove(actress);
                                    Toast.makeText(mActivity, "已取消收藏", Toast.LENGTH_SHORT).show();
                                } else {
                                    Collections.reverse(actresses);
                                    actresses.add(actress);
                                    Collections.reverse(actresses);
                                    Toast.makeText(mActivity, "已收藏", Toast.LENGTH_SHORT).show();
                                }
                                JAViewer.CONFIGURATIONS.save();
                                FavouriteTabsFragment.update();
                                break;
                            }
                        }
                    }
                });
        builder.create().show();
        return true;
    }
}
