package io.github.javiewer.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface AVMO {

    String BASE_URL = "https://avmo.pw";
    String LANGUAGE_NODE = "/ja";

    @GET(AVMO.LANGUAGE_NODE + "/page/{page}")
    Call<ResponseBody> getHomePage(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/released/page/{page}")
    Call<ResponseBody> getReleased(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/popular/page/{page}")
    Call<ResponseBody> getPopular(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/actresses/page/{page}")
    Call<ResponseBody> getActresses(@Path("page") int page);

    @GET(AVMO.LANGUAGE_NODE + "/genren")
    Call<ResponseBody> getGenren();

    @GET
    Call<ResponseBody> get(@Url String url);

}
