package io.github.javiewer;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.github.javiewer.adapter.item.DataSource;
import io.github.javiewer.fragment.ActressesFragment;
import io.github.javiewer.fragment.HomeFragment;
import io.github.javiewer.fragment.PopularFragment;
import io.github.javiewer.fragment.ReleasedFragment;
import io.github.javiewer.fragment.favourite.FavouriteTabsFragment;
import io.github.javiewer.fragment.genre.GenreTabsFragment;
import io.github.javiewer.network.BasicService;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Project: JAViewer
 */

public class JAViewer extends Application {
    static {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                FirebaseCrash.report(e);
            }
        });
    }

    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 5.1.1; Nexus 5 Build/LMY48B; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/43.0.2357.65 Mobile Safari/537.36";

    public static Configurations CONFIGURATIONS;

    public static final List<DataSource> DATA_SOURCES = new ArrayList<DataSource>() {{
        add(new DataSource("AVMOO 日本", "https://avmoo.com"));
        add(new DataSource("AVSOX 日本无码", "https://avso.pw"));
        add(new DataSource("AVMEMO 欧美", "https://avxo.pw"));
    }};

    public static final Map<Integer, Class<? extends Fragment>> FRAGMENTS = new HashMap<Integer, Class<? extends Fragment>>() {{
        put(R.id.nav_home, HomeFragment.class);
        put(R.id.nav_popular, PopularFragment.class);
        put(R.id.nav_released, ReleasedFragment.class);
        put(R.id.nav_actresses, ActressesFragment.class);
        put(R.id.nav_genre, GenreTabsFragment.class);
        put(R.id.nav_favourite, FavouriteTabsFragment.class);
    }};

    public static DataSource getDataSource() {
        return JAViewer.CONFIGURATIONS.getDataSource();
    }

    public static BasicService SERVICE;

    public static void recreateService() {
        SERVICE = new Retrofit.Builder()
                .baseUrl(JAViewer.getDataSource().getLink())
                .client(JAViewer.HTTP_CLIENT)
                .build()
                .create(BasicService.class);
    }

    public static File getStorageDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), "JAViewer/");
        dir.mkdirs();
        return dir;
    }

    public static HttpUrl replaceUrl(HttpUrl url) {
        HttpUrl.Builder builder = url.newBuilder();
        if (url.url().getHost().equals("avmo.pw")) {
            builder.host("avmoo.com");
            return builder.build();
        }

        return url;
    }

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .url(replaceUrl(original.url()))
                    .header("User-Agent", USER_AGENT)
                    .build();

            return chain.proceed(request);
        }
    })
            .cookieJar(new CookieJar() {
                private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url, cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url);
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    public static <T> T parseJson(Class<T> beanClass, JsonReader reader) throws JsonParseException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(reader, beanClass);
    }

    public static <T> T parseJson(Class<T> beanClass, String json) throws JsonParseException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(json, beanClass);
    }

    public static boolean Objects_equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static void a(Context context) {
        String url = "https://qr.alipay.com/a6x05027ymf6n8kl0qkoa54";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        return;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CustomActivityOnCrash.install(this);
    }
}
