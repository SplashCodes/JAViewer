package io.github.javiewer.adapter.item;

/**
 * Created by MagicDroidX on 2016/7/23.
 */
public class MagnetLink {

    protected String magnetLink;

    public static MagnetLink create(String magnetLink) {
        MagnetLink magnet = new MagnetLink();
        magnet.magnetLink = magnetLink;
        return magnet;
    }

    public String getMagnetLink() {
        return magnetLink;
    }
}
