package io.github.javiewer.network;

import io.github.javiewer.Javiewer;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface AVMO {

    AVMO INSTANCE = new Retrofit.Builder()
            .baseUrl(AVMO.BASE_URL)
            .client(Javiewer.HTTP_CLIENT)
            .build()
            .create(AVMO.class);

    String BASE_URL = "https://avmo.pw";
    String LANGUAGE_NODE = "/cn";

    @GET(AVMO.LANGUAGE_NODE + "/page/{page}")
    Call<ResponseBody> getHomePage(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/released/page/{page}")
    Call<ResponseBody> getReleased(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/popular/page/{page}")
    Call<ResponseBody> getPopular(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/actresses/page/{page}")
    Call<ResponseBody> getActresses(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/genre")
    Call<ResponseBody> getGenre();

    @GET
    Call<ResponseBody> get(@Url String url);

}
