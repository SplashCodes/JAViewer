package io.github.javiewer.adapter.item;

/**
 * Project: JAViewer
 */
public class Actress extends Linkable {

    protected String name;
    protected String imageUrl;

    public static Actress create(String name, String imageUrl, String detailUrl) {
        Actress actress = new Actress();
        actress.name = name;
        actress.imageUrl = imageUrl;
        actress.link = detailUrl;
        return actress;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
