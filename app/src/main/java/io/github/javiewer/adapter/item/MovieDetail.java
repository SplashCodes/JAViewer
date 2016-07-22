package io.github.javiewer.adapter.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Project: JAViewer
 */
public class MovieDetail {

    public String title;

    public String coverUrl;

    public final List<Screenshot> screenshots = new ArrayList<>();

    public String code = "未知";

    public String date = "未知";

    public String duration = "未知";
}
