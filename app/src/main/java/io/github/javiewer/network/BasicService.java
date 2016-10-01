package io.github.javiewer.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

/**
 * Project: JAViewer
 */
public interface BasicService {

    String LANGUAGE_NODE = "/cn";

    @GET(BasicService.LANGUAGE_NODE + "/page/{page}")
    Call<ResponseBody> getHomePage(@Path("page") int page);

    @GET(BasicService.LANGUAGE_NODE + "/released/page/{page}")
    Call<ResponseBody> getReleased(@Path("page") int page);

    @GET(BasicService.LANGUAGE_NODE + "/popular/page/{page}")
    Call<ResponseBody> getPopular(@Path("page") int page);

    @GET(BasicService.LANGUAGE_NODE + "/actresses/page/{page}")
    Call<ResponseBody> getActresses(@Path("page") int page);

    @GET(BasicService.LANGUAGE_NODE + "/genre")
    Call<ResponseBody> getGenre();

    @GET
    Call<ResponseBody> get(@Url String url);

}
