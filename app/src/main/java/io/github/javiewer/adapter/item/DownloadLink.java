package io.github.javiewer.adapter.item;

/**
 * Created by MagicDroidX on 2016/7/22.
 */
public class DownloadLink extends Linkable {
    protected String title;
    protected String size;
    protected String date;

    public static DownloadLink create(String title, String size, String date, String link) {
        DownloadLink download = new DownloadLink();
        download.title = title;
        download.size = size;
        download.date = date;
        download.link = link;
        return download;
    }

    public String getTitle() {
        return title;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }
}
