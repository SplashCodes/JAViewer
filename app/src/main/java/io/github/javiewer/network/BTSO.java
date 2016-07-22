package io.github.javiewer.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Project: JAViewer
 */
public interface BTSO {

    String BASE_URL = "https://btso.pw";

    @GET("/search/{keyword}/page/{page}")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Path(value = "keyword") String keyword, @Path("page") int page);
}
