package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.javiewer.network.AVMO;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Project: JAViewer
 */
public class MovieListFragment extends MovieFragment {

    public String query;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        this.query = bundle.getString("query");
    }

    @Override
    public Call<ResponseBody> getCall(int page) {
        return AVMO.INSTANCE.get(this.query + "/page/" + page);
    }
}
