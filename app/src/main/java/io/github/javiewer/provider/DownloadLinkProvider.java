package io.github.javiewer.provider;

import java.util.ArrayList;
import java.util.List;

import io.github.javiewer.adapter.item.DownloadLink;

/**
 * Created by MagicDroidX on 2016/7/22.
 */
public abstract class DownloadLinkProvider {
    public abstract List<DownloadLink> parseDownloadLinks(String htmlContent);

    public static DownloadLinkProvider getProvider(String name) {
        switch (name.toLowerCase().trim()) {
            case "btso":
                return new BTSOLinkProvider();
            default:
                return null;
        }
    }
}
