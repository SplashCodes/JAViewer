package io.github.javiewer.adapter.item;

import java.util.regex.Pattern;

import io.github.javiewer.network.AVMO;

/**
 * Project: JAViewer
 */
public class Movie extends Linkable {

    protected String title;
    protected String code;
    protected String coverUrl;
    protected String date;
    protected boolean hot;

    public static Movie create(String title, String code, String date, String coverUrl, String detailUrl, boolean hot) {
        Movie movie = new Movie();
        movie.title = title;
        movie.date = date;
        movie.code = code;
        movie.coverUrl = coverUrl;
        movie.hot = hot;
        movie.link = detailUrl;
        return movie;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public boolean isHot() {
        return hot;
    }
}
