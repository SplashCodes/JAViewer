package io.github.javiewer.fragment;

import io.github.javiewer.JAViewer;
import io.github.javiewer.network.BasicService;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class HomeFragment extends MovieFragment {
    @Override
    public Call<ResponseBody> newCall(int page) {
        return  JAViewer.SERVICE.getHomePage(page);
    }
}
