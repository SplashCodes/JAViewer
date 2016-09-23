package io.github.javiewer;

import java.io.IOException;

import io.github.javiewer.network.BTSO;
import io.github.javiewer.network.TorrentKitty;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by MagicDroidX on 2016/9/23.
 */

public class Javiewer {
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("User-Agent", USER_AGENT)
                    .build();

            return chain.proceed(request);
        }
    }).build();

    public static final Retrofit RETROFIT_BTSO = new Retrofit.Builder()
            .baseUrl(BTSO.BASE_URL)
            .client(HTTP_CLIENT)
            .build();

    public static final Retrofit RETROFIT_TORRENT_KITTY = new Retrofit.Builder()
            .baseUrl(TorrentKitty.BASE_URL)
            .client(HTTP_CLIENT)
            .build();
}
