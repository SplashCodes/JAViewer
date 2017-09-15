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

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            return true;
        }

        if (obj instanceof Actress) {
            return this.name.equals(((Actress) obj).getName());
        }

        return false;
    }
}
