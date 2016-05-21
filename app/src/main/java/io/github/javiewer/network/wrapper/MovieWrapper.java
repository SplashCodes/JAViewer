package io.github.javiewer.network.wrapper;

/**
 * Author: MagicDroidX
 */
public class MovieWrapper {

    public String title;

    public String code;

    public String image;

    public String time;

    public boolean hot;

    public MovieWrapper(String title, String code, String time, String image, boolean hot) {
        this.title = title;
        this.time = time;
        this.code = code;
        this.image = image;
        this.hot = hot;
    }
}
