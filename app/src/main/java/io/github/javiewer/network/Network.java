package io.github.javiewer.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Project: JAViewer
 */
public interface Network {

    @GET("/cn/currentPage/{page}")
    Call<ResponseBody> getHomePage(@Path("page") int page);

    @GET("/cn/released/currentPage/{page}")
    Call<ResponseBody> getReleased(@Path("page") int page);

    @GET("/cn/popular/currentPage/{page}")
    Call<ResponseBody> getPopular(@Path("page") int page);

    @GET("/cn/actresses")
    Call<ResponseBody> getActresses();

    @GET("/cn/genren")
    Call<ResponseBody> getGenren();

    @GET("/cn/search/{keyword}")
    Call<ResponseBody> getSearchResult(@Path("keyword") String keyword);

    @GET("/cn/movie/{hash}")
    Call<ResponseBody> getMovieDetail(@Path("hash") String movieHash);

    @GET("/cn/{query}/currentPage/{page}")
    Call<ResponseBody> query(@Path(value = "query", encoded = true) String query, @Path("page") int page);
}
