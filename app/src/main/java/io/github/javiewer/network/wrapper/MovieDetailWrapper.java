package io.github.javiewer.network.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: JAViewer
 */
public class MovieDetailWrapper {

    public String title;

    public String coverUrl;

    public final List<ScreenshotWrapper> screenshots = new ArrayList<>();
}
