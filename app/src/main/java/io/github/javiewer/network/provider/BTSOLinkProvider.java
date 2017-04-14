package io.github.javiewer.network.provider;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.DownloadLink;
import io.github.javiewer.adapter.item.MagnetLink;
import io.github.javiewer.network.BTSO;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class BTSOLinkProvider extends DownloadLinkProvider {


    @Override
    public Call<ResponseBody> search(String keyword, int page) {
        return BTSO.INSTANCE.search(keyword, page);
    }

    @Override
    public List<DownloadLink> parseDownloadLinks(String htmlContent) {
        ArrayList<DownloadLink> links = new ArrayList<>();
        Elements rows = Jsoup.parse(htmlContent).getElementsByClass("row");
        for (Element row : rows) {
            try {
                Element a = row.getElementsByTag("a").first();
                links.add(
                        DownloadLink.create(
                                row.getElementsByClass("file").first().text(),
                                row.getElementsByClass("size").first().text(),
                                row.getElementsByClass("date").first().text(),
                                a.attr("href"),
                                null)
                );
            } catch (Exception ignored) {

            }
        }
        return links;
    }

    @Override
    public Call<ResponseBody> get(String url) {
        return BTSO.INSTANCE.get(url);
    }

    @Override
    public MagnetLink parseMagnetLink(String htmlContent) {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementsByClass("magnet-link").first().text());
    }
}
