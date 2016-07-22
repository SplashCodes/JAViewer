package io.github.javiewer.adapter.item;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.javiewer.network.AVMO;

/**
 * Project: JAViewer
 */
public class Actress extends Linkable {

    protected String name;
    protected String imageUrl;

    static final Pattern pattern = Pattern.compile(AVMO.BASE_URL + AVMO.LANGUAGE_NODE + "/(.*)");

    public static Actress create(String name, String imageUrl, String queryUrl) {
        Actress actress = new Actress();
        actress.name = name;
        actress.imageUrl = imageUrl;

        Matcher matcher = pattern.matcher(queryUrl);
        if (matcher.find()) {
            actress.link = matcher.group(1);
        }

        return actress;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
