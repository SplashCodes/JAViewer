package io.github.javiewer.network;

import io.github.javiewer.JAViewer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface BTSO {

    String BASE_URL = "https://api.rekonquer.com";
    BTSO INSTANCE = new Retrofit.Builder()
            .baseUrl(BTSO.BASE_URL)
            .client(JAViewer.HTTP_CLIENT)
            .build()
            .create(BTSO.class);

    @GET("/btso.php")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> search(@Query(value = "kw") String keyword, @Query("page") int page);

    @GET
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<ResponseBody> get(@Url String url);
}
