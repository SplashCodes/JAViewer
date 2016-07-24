package io.github.javiewer.network.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.adapter.item.Screenshot;

/**
 * Project: JAViewer
 */
public class AVMOProvider {

    public static List<Movie> parseMovies(String html) {
        Document document = Jsoup.parse(html);

        Elements items = document.getElementsByClass("item");

        List<Movie> movies = new ArrayList<>();

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

            movies.add(Movie.create(
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

    public static List<Actress> parseActresses(String html) {
        Document document = Jsoup.parse(html);

        Elements items = document.getElementsByClass("item");

        List<Actress> actresses = new ArrayList<>();

        for (Element item : items) {
            Element box = item.getElementsByClass("avatar-box").first();
            Element frame = box.getElementsByClass("photo-frame").first();
            Element info = box.getElementsByClass("photo-info").first();

            Element img = frame.getElementsByTag("img").first();
            Element span = info.getElementsByTag("span").first();

            actresses.add(Actress.create(
                    span.text(),
                    img.attr("src"),
                    box.attr("href")
            ));
        }

        return actresses;
    }

    public static MovieDetail parseMoviesDetail(String html) {
        final String headerCode = "品番";
        final String headerDate = "発売日";
        final String headerDuration = "収録時間";

        Document document = Jsoup.parse(html);

        MovieDetail movie = new MovieDetail();

        movie.title = document.getElementsByTag("h3").first().text();
        movie.coverUrl = document.getElementsByClass("bigImage").first().attr("href");

        for (Element element : document.getElementsByClass("sample-box")) {
            movie.screenshots.add(Screenshot.create(element.getElementsByTag("img").first().attr("src"), element.attr("href")));
        }

        Element info = document.getElementsByClass("col-md-3").first();
        for (Element p : info.getElementsByTag("p")) {

            String[] s = p.text().split(":");

            if (s.length > 1) {

                String content = s[1].replace(" ", "");

                if (s[0].contains(headerCode)) {
                    movie.code = content;
                } else if (s[0].contains(headerDate)) {
                    movie.date = content;
                } else if (s[0].contains(headerDuration)) {
                    movie.duration = content;
                }
            }
        }

        return movie;
    }
}
