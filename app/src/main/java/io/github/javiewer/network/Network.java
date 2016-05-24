package io.github.javiewer.network;

import io.github.javiewer.activity.MainActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Project: JAViewer
 */
public interface Network {

    @GET(MainActivity.LANGUAGE_NODE + "/currentPage/{page}")
    Call<ResponseBody> getHomePage(@Path("page") int page);

    @GET(MainActivity.LANGUAGE_NODE + "/released/currentPage/{page}")
    Call<ResponseBody> getReleased(@Path("page") int page);

    @GET(MainActivity.LANGUAGE_NODE + "/popular/currentPage/{page}")
    Call<ResponseBody> getPopular(@Path("page") int page);

    @GET(MainActivity.LANGUAGE_NODE + "/actresses/currentPage/{page}")
    Call<ResponseBody> getActresses(@Path("page") int page);

    @GET(MainActivity.LANGUAGE_NODE + "/genren")
    Call<ResponseBody> getGenren();

    @GET(MainActivity.LANGUAGE_NODE + "/{query}")
    Call<ResponseBody> query(@Path(value = "query", encoded = true) String query);

    @GET(MainActivity.LANGUAGE_NODE + "/{query}/currentPage/{page}")
    Call<ResponseBody> query(@Path(value = "query", encoded = true) String query, @Path("page") int page);
}
