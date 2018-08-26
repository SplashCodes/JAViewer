package io.github.javiewer.network.item;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Project: JAViewer
 */
public class AvgleSearchResult {

    public boolean success;
    public Response response;

    public static class Response {

        public boolean has_more;
        public int total_videos;
        public int current_offset;
        public int limit;
        public List<Video> videos;

        public static class Video {
            public String title;
            public String keyword;
            public String channel;
            public double duration;
            public double framerate;
            public boolean hd;
            public int addtime;
            public int viewnumber;
            public int likes;
            public int dislikes;
            public String video_url;
            public String embedded_url;
            public String preview_url;
            public String preview_video_url;
            @SerializedName("public")
            public boolean isPublic;
            public String vid;
            public String uid;
        }
    }
}
