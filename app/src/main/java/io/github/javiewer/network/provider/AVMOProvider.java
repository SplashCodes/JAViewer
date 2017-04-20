package io.github.javiewer.network.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.github.javiewer.adapter.item.Actress;
import io.github.javiewer.adapter.item.Genre;
import io.github.javiewer.adapter.item.Movie;
import io.github.javiewer.adapter.item.MovieDetail;
import io.github.javiewer.adapter.item.Screenshot;

/**
 * Project: JAViewer
 */
public class AVMOProvider {

    public static List<Movie> parseMovies(String html) {
        Document document = Jsoup.parse(html);

        List<Movie> movies = new ArrayList<>();

        for (Element box : document.select("a[class*=movie-box]")) {
            Element img = box.select("div.photo-frame > img").first();
            Element span = box.select("div.photo-info > span").first();

            boolean hot = span.getElementsByTag("i").size() > 0;

            Elements date = span.select("date");

            movies.add(
                    Movie.create(
                            img.attr("title"),  //标题
                            date.get(0).text(), //番号
                            date.get(1).text(), //日期
                            img.attr("src"),    //图片地址
                            box.attr("href"),   //链接
                            hot                 //是否热门
                    )
            );
        }

        return movies;
    }

    public static List<Actress> parseActresses(String html) {
        Document document = Jsoup.parse(html);

        List<Actress> actresses = new ArrayList<>();

        for (Element box : document.select("a[class*=avatar-box]")) {
            Element img = box.select("div.photo-frame > img").first();
            Element span = box.select("div.photo-info > span").first();

            actresses.add(
                    Actress.create(
                            span.text(),     //名字
                            img.attr("src"), //图片地址
                            box.attr("href") //链接
                    ));
        }

        return actresses;
    }

    public static MovieDetail parseMoviesDetail(String html) {
        Document document = Jsoup.parse(html);
        MovieDetail movie = new MovieDetail();

        //General Parsing
        {
            movie.title = document.select("div.container > h3").first().text();
            movie.coverUrl = document.select("[class=bigImage]").first().attr("href");
        }

        //Parsing Screenshots
        {
            for (Element box : document.select("[class*=sample-box]")) {
                movie.screenshots.add(
                        Screenshot.create(
                                box.getElementsByTag("img").first().attr("src"),
                                box.attr("href")
                        )
                );
            }
        }

        //Parsing Actresses
        {
            for (Element box : document.select("[class*=avatar-box]")) {
                movie.actresses.add(
                        Actress.create(
                                box.text(),
                                box.getElementsByTag("img").first().attr("src"),
                                box.attr("href")
                        )
                );
            }
        }

        //Parsing Headers
        {
            Element info = document.select("div.info").first();
            if (info != null) {
                for (Element p : info.select("p:not([class*=header]):has(span:not([class=genre]))")) {
                    String[] strings = p.text().split(":");
                    movie.headers.add(MovieDetail.Header.create(
                            strings[0].trim(),
                            strings.length > 1 ? strings[1].trim() : "",
                            null
                    ));
                }

                {
                    List<String> headerNames = new ArrayList<>();
                    List<String[]> headerAttr = new ArrayList<>();

                    for (Element p : info.select("p[class*=header]")) {
                        headerNames.add(p.text().replace(":", ""));
                    }

                    for (Element a : info.select("p > a")) {
                        headerAttr.add(new String[]{a.text(), a.attr("href")});
                    }

                    for (int i = 0; i < Math.min(headerNames.size(), headerAttr.size()); i++) {
                        movie.headers.add(
                                MovieDetail.Header.create(
                                        headerNames.get(i),
                                        headerAttr.get(i)[0].trim(),
                                        headerAttr.get(i)[1].trim()
                                )
                        );
                    }
                }

                for (Element a : info.select("* > [class=genre] > a")) {
                    movie.genres.add(
                            Genre.create(
                                    a.text(),
                                    a.attr("href")
                            )
                    );
                }
            }
            return movie;
        }
    }

    public static LinkedHashMap<String, List<Genre>> parseGenres(String html) {
        LinkedHashMap<String, List<Genre>> map = new LinkedHashMap<>();

        Element container = Jsoup.parse(html).getElementsByClass("pt-10").first();
        List<String> keys = new ArrayList<>();
        for (Element e : container.getElementsByTag("h4")) {
            keys.add(e.text());
        }

        List<List<Genre>> genres = new ArrayList<>();
        for (Element element : container.getElementsByClass("genre-box")) {
            List<Genre> list = new ArrayList<>();
            for (Element e : element.getElementsByTag("a")) {
                list.add(Genre.create(e.text(), e.attr("href")));
            }
            genres.add(list);
        }

        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i), genres.get(i));
        }

        return map;
    }
}
