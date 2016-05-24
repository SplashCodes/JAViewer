package io.github.javiewer.network.wrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.javiewer.activity.MainActivity;

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

    public static Pattern pattern = Pattern.compile(MainActivity.SOURCE_URL + MainActivity.LANGUAGE_NODE + "/(.*)");

    public MovieWrapper(String title, String code, String time, String imageUrl, String detailUrl, boolean hot) {
        this.title = title;
        this.time = time;
        this.code = code;
        this.imageUrl = imageUrl;
        this.hot = hot;

        Matcher matcher = pattern.matcher(detailUrl);
        if (matcher.find()) {
            this.detailUrl = matcher.group(1);
        }
    }
}
