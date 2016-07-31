package io.github.javiewer.adapter.item;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MagicDroidX on 2016/7/24.
 */
public class Genre extends Linkable{
    protected String title;

    public static Genre create(String title, String link) {
        Genre genre = new Genre();
        genre.title = title;
        genre.link = link;
        return genre;
    }

    public String getTitle() {
        return title;
    }
}
