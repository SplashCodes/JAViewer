package io.github.javiewer;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

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
    public static final String VERSION = "1.0";

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

    public static final DisplayImageOptions DISPLAY_IMAGE_OPTIONS = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading()
            .cacheInMemory()
            .cacheOnDisc()
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
            .bitmapConfig(Bitmap.Config.ARGB_8888) // default
            .delayBeforeLoading(1000)
            .displayer(new FadeInBitmapDisplayer(500)) // default
            .build();

    public static <T> T parseJson(Class<T> beanClass, String json) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, beanClass);
    }
}
