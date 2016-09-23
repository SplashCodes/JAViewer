package io.github.javiewer.fragment;

import io.github.javiewer.network.AVMO;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class HomeFragment extends MovieFragment {
    @Override
    public Call<ResponseBody> getCall(int page) {
        return AVMO.INSTANCE.getHomePage(page);
    }
}
