package io.github.javiewer.adapter.item;

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

}
