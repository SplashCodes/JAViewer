package io.github.javiewer.network;

import io.github.javiewer.JAViewer;
import io.github.javiewer.network.item.AvgleSearchResult;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Project: JAViewer
 */
public interface Avgle {

    String BASE_URL = "https://api.avgle.com";
    Avgle INSTANCE = new Retrofit.Builder()
            .baseUrl(Avgle.BASE_URL)
            .client(JAViewer.HTTP_CLIENT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Avgle.class);

    @GET("/v1/search/{keyword}/0?limit=1")
    @Headers("Accept-Language: zh-CN,zh;q=0.8,en;q=0.6")
    Call<AvgleSearchResult> search(@Path(value = "keyword") String keyword);

    @GET("/{path}")
    Call<ResponseBody> get(@Path("path") String path);
}
