package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.github.javiewer.activity.MainActivity;
import io.github.javiewer.network.Network;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Project: JAViewer
 */
public class QueryFragment extends MovieFragment {

    public String query;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        this.query = bundle.getString("query");
    }

    @Override
    public Call<ResponseBody> getCall(int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.SOURCE_URL)
                .build();

        Network network = retrofit.create(Network.class);

        return network.query(this.query, page);
    }
}
