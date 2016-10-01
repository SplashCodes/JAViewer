package io.github.javiewer.adapter.item;

import io.github.javiewer.JAViewer;

/**
 * Project: JAViewer
 */

public class DataSource extends Linkable {
    public String name;

    public DataSource(String name, String baseUrl) {
        this.name = name;
        this.link = baseUrl;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataSource) {
            return JAViewer.Objects_equals(((DataSource) obj).getLink(), getLink());
        }

        return false;
    }
}
