package io.github.javiewer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.javiewer.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebViewActivity extends AppCompatActivity {

    public static Intent newIntent(Context context, String embeddedUrl) {
        Intent intent = new Intent(context, WebViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("embedded_url", embeddedUrl);
        intent.putExtras(bundle);
        return intent;
    }

    public static OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request original = chain.request();

            Request request = original.newBuilder()
                    .header("Connection", " keep-alive")
                    .header("Accept", " */*")
                    .header("X-Requested-With", " XMLHttpRequest")
                    .header("User-Agent", " Mozilla/5.0 (Windows NT 10.0 Win64 x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36")
                    .header("Accept-Language", "zhCN,zh;q=0.8,en-US;q=0.5,cnq=0.3")
                    .build();

            return chain.proceed(request);
        }
    }).build();

    @BindView(R.id.web_view)
    WebView mWebView;

    String embeddedUrl;
    boolean locked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        ButterKnife.bind(this);

        Bundle bundle = this.getIntent().getExtras();
        embeddedUrl = bundle.getString("embedded_url");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CookieManager cookieManager = CookieManager.getInstance();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }

        mWebView.loadDataWithBaseURL(
                "http://javiewer.github.io/player.html",
                "<iframe width=\"100%\" height=\"100%\" src=\"" + embeddedUrl + "\" frameborder=\"0\" allowfullscreen></iframe>",
                "text/html",
                null,
                null
        );

        mWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return locked;
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if (url.contains("?hash=")) {
                    String cookie = cookieManager.getCookie(url);

                    final Request request = new Request.Builder()
                            .url(url)
                            .header("Referer", "https://javiewer.github.com/player.html")
                            .header("Cookie", cookie)
                            .get()
                            .build();

                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (isFinishing()) {
                                return;
                            }

                            Gson gson = new Gson();
                            String json = response.body().string();
                            JsonObject object = gson.fromJson(json, JsonObject.class);
                            String playBack = object.get("url").getAsString();
                            testVideoPlayBack(playBack);
                            //Log.i("VideoPlayBack", playBack);
                        }
                    });
                }

                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    void testVideoPlayBack(String url) {
        final Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (isFinishing()) {
                    return;
                }

                if (response.code() == 200) {
                    Intent intent = new Intent();
                    intent.putExtra("m3u8", response.request().url().toString());
                    setResult(RESULT_OK, intent);

                    WebViewActivity.this.finish();
                }
            }
        });
    }

    @OnClick(R.id.button_unlock)
    public void onUnlock(Button button) {
        locked = false;
        button.setEnabled(false);
        Toast.makeText(this, "锁定已解除，请完成验证码，不要按任何其他地方！", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}