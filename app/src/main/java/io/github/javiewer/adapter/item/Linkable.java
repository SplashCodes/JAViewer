package io.github.javiewer.adapter.item;

import java.io.Serializable;

import static io.github.javiewer.JAViewer.Objects_equals;

/**
 * Project: JAViewer
 */
public class Linkable implements Serializable {

    public String link;

    public String getLink() {
        return link;
    }

    @Override
    public boolean equals(Object linkable) {
        if (!(linkable instanceof Linkable)) {
            return false;
        }

        return Objects_equals(link, ((Linkable) linkable).link);
    }

    @Override
    public String toString() {
        return "Linkable{" +
                "link='" + link + '\'' +
                '}';
    }
}
