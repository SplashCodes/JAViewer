package io.github.javiewer.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.DownloadLink;

/**
 * Created by MagicDroidX on 2016/7/22.
 */
public class BTSOLinkProvider extends DownloadLinkProvider {
    @Override
    public List<DownloadLink> parseDownloadLinks(String htmlContent) {
        ArrayList<DownloadLink> links = new ArrayList<>();
        Elements rows = Jsoup.parse(htmlContent).getElementsByClass("row");
        for (Element row : rows) {
            try {
                Element a = row.getElementsByTag("a").first();
                links.add(DownloadLink.create(
                        row.getElementsByClass("file").first().text(),
                        row.getElementsByClass("size").first().text(),
                        row.getElementsByClass("date").first().text(),
                        a.getElementsByAttribute("href").first().text()));
            } catch (Exception ignored) {

            }
        }
        return links;
    }
}
