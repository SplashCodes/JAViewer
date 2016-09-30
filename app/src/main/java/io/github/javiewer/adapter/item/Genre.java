package io.github.javiewer.adapter.item;

/**
 * Project: JAViewer
 */
public class Genre extends Linkable {
    public String name;

    public static Genre create(String name, String link) {
        Genre genre = new Genre();
        genre.name = name;
        genre.link = link;
        return genre;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + ":" + link;
    }
}
