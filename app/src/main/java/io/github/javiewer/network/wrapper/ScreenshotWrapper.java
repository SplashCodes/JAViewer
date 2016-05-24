package io.github.javiewer.network.wrapper;

/**
 * Project: JAViewer
 */
public class ScreenshotWrapper {

    public String thumbnailUrl;

    public String imageUrl;

    public ScreenshotWrapper() {

    }

    public ScreenshotWrapper(String thumbnailUrl, String imageUrl) {
        this.thumbnailUrl = thumbnailUrl;
        this.imageUrl = imageUrl;
    }
}
