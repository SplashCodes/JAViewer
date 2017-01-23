package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.javiewer.JAViewer;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class MovieListFragment extends MovieFragment {

    public String link;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        this.link = bundle.getString("link");
    }

    @Override
    public Call<ResponseBody> newCall(int page) {
        return JAViewer.SERVICE.get(this.link + "/page/" + page);
    }
}
