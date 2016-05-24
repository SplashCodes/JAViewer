package io.github.javiewer.network.converter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.network.wrapper.ActressWrapper;
import io.github.javiewer.network.wrapper.MovieDetailWrapper;
import io.github.javiewer.network.wrapper.MovieWrapper;
import io.github.javiewer.network.wrapper.ScreenshotWrapper;

/**
 * Project: JAViewer
 */
public class HtmlConverter {

    public static List<MovieWrapper> parseMovies(String html) {
        Document document = Jsoup.parse(html);

        Elements items = document.getElementsByClass("item");

        List<MovieWrapper> movies = new ArrayList<>();

        for (Element item : items) {
            Element box = item.getElementsByClass("movie-box").first();

            if (box == null) {
                continue;
            }

            Element frame = box.getElementsByClass("photo-frame").first();
            Element info = box.getElementsByClass("photo-info").first();

            Element img = frame.getElementsByTag("img").first();
            Element span = info.getElementsByTag("span").first();

            boolean hot = span.getElementsByTag("i").size() > 0;

            movies.add(new MovieWrapper(
                    img.attr("title"),
                    hot ? span.child(2).text() : span.child(1).text(),
                    hot ? span.child(3).text() : span.child(2).text(),
                    img.attr("src"),
                    box.attr("href"),
                    hot
            ));
        }

        return movies;
    }

    public static List<ActressWrapper> parseActresses(String html) {
        Document document = Jsoup.parse(html);

        Elements items = document.getElementsByClass("item");

        List<ActressWrapper> actresses = new ArrayList<>();

        for (Element item : items) {
            Element box = item.getElementsByClass("avatar-box").first();
            Element frame = box.getElementsByClass("photo-frame").first();
            Element info = box.getElementsByClass("photo-info").first();

            Element img = frame.getElementsByTag("img").first();
            Element span = info.getElementsByTag("span").first();

            actresses.add(new ActressWrapper(
                    span.text(),
                    img.attr("src"),
                    box.attr("href")
            ));
        }

        return actresses;
    }

    public static MovieDetailWrapper parseMoviesDetail(String html) {
        Document document = Jsoup.parse(html);

        MovieDetailWrapper movie = new MovieDetailWrapper();

        movie.title = document.getElementsByTag("h3").first().text();
        movie.coverUrl = document.getElementsByClass("bigImage").first().attr("href");

        for (Element element : document.getElementsByClass("sample-box")) {
            movie.screenshots.add(new ScreenshotWrapper(element.getElementsByTag("img").first().attr("src"), element.attr("href")));
        }

        return movie;
    }
}
