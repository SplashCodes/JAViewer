package io.github.javiewer.network.wrapper;

/**
 * Project: JAViewer
 */
public class MovieWrapper {

    public String title;

    public String code;

    public String imageUrl;

    public String time;

    public boolean hot;

    public String detailUrl;

    public MovieWrapper(String title, String code, String time, String imageUrl, String detailUrl, boolean hot) {
        this.title = title;
        this.time = time;
        this.code = code;
        this.imageUrl = imageUrl;
        this.detailUrl = detailUrl;
        this.hot = hot;
    }
}
