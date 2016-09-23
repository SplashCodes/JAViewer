package io.github.javiewer.network;

import io.github.javiewer.Javiewer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface TorrentKitty {

    TorrentKitty INSTANCE = new Retrofit.Builder()
            .baseUrl(TorrentKitty.BASE_URL)
            .client(Javiewer.HTTP_CLIENT)
            .build()
            .create(TorrentKitty.class);

    String BASE_URL = "https://www.torrentkitty.tv";

    @GET("/search/{keyword}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Path(value = "keyword") String keyword);

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> get(@Url String url);
}
