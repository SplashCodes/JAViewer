package io.github.javiewer.network.wrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.javiewer.activity.MainActivity;

/**
 * Project: JAViewer
 */
public class ActressWrapper {

    public String name;

    public String imageUrl;

    public String queryUrl;

    public static Pattern pattern = Pattern.compile(MainActivity.SOURCE_URL + MainActivity.LANGUAGE_NODE + "/(.*)");

    public ActressWrapper(String name, String imageUrl, String queryUrl) {
        this.name = name;
        this.imageUrl = imageUrl;

        Matcher matcher = pattern.matcher(queryUrl);
        if (matcher.find()) {
            this.queryUrl = matcher.group(1);
        }
    }
}
