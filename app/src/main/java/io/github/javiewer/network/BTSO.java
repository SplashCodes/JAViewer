package io.github.javiewer.network;

import io.github.javiewer.JAViewer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface BTSO {

    @GET("/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Path(value = "keyword") String keyword, @Path("page") int page);

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> get(@Url String url);
}
