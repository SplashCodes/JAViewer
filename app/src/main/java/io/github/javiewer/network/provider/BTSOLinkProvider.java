package io.github.javiewer.network.provider;

import android.util.Log;

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
import retrofit2.Retrofit;

/**
 * Created by MagicDroidX on 2016/7/22.
 */
public class BTSOLinkProvider extends DownloadLinkProvider {


    @Override
    public Call<ResponseBody> search(String keyword, int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BTSO.BASE_URL)
                .build();
        BTSO btso = retrofit.create(BTSO.class);
        return btso.search(keyword, page);
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
                                "文件大小：" + row.getElementsByClass("size").first().text(),
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
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BTSO.BASE_URL)
                .build();
        BTSO btso = retrofit.create(BTSO.class);
        return btso.get(url);
    }

    @Override
    public MagnetLink parseMagnetLink(String htmlContent) {
        return MagnetLink.create(Jsoup.parse(htmlContent).getElementsByClass("magnet-link").first().text());
    }
}
