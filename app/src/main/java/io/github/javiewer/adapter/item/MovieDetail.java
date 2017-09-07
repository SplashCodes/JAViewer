package io.github.javiewer.adapter.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: JAViewer
 */
public class MovieDetail {

    public String title;

    public String coverUrl;

    public final List<Screenshot> screenshots = new ArrayList<>();

    public List<Header> headers = new ArrayList<>();

    public List<Genre> genres = new ArrayList<>();

    public List<Actress> actresses = new ArrayList<>();

    public static class Header extends Linkable {
        public String name;
        public String value;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public static Header create(String name, String value, String link) {
            Header header = new Header();
            header.name = name;
            header.value = value;
            header.link = link;
            return header;
        }

        @Override
        public String toString() {
            return "Header{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
