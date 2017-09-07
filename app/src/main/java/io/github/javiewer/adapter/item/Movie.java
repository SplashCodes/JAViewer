package io.github.javiewer.adapter.item;

/**
 * Project: JAViewer
 */
public class Movie extends Linkable {

    public String title;
    public String code;
    public String coverUrl;
    public String date;
    public boolean hot;

    public static Movie create(String title, String code, String date, String coverUrl, String detailUrl, boolean hot) {
        Movie movie = new Movie();
        movie.title = title;
        movie.date = date;
        movie.code = code;
        movie.coverUrl = coverUrl;
        movie.hot = hot;
        movie.link = detailUrl;
        return movie;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getCode() {
        return code;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public boolean isHot() {
        return hot;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", code='" + code + '\'' +
                ", coverUrl='" + coverUrl + '\'' +
                ", date='" + date + '\'' +
                ", hot=" + hot +
                '}';
    }
}
