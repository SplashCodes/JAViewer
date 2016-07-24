package io.github.javiewer.adapter.item;

/**
 * Created by MagicDroidX on 2016/7/22.
 */
public class DownloadLink extends Linkable {
    protected String title;
    protected String size;
    protected String date;
    protected MagnetLink magnetLink;

    public static DownloadLink create(String title, String size, String date, String link, String magnetLink) {
        DownloadLink download = new DownloadLink();
        download.title = title;
        download.size = size;
        download.date = date;
        download.link = link;
        download.magnetLink = MagnetLink.create(magnetLink);
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

    public boolean hasMagnetLink() {
        return magnetLink.getMagnetLink() != null;
    }

    public String getMagnetLink() {
        return magnetLink.getMagnetLink();
    }
}
