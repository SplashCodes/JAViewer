package io.github.javiewer.fragment;

import io.github.javiewer.JAViewer;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class PopularFragment extends MovieFragment {
    @Override
    public Call<ResponseBody> newCall(int page) {
        return JAViewer.SERVICE.getPopular(page);
    }
}
