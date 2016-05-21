package io.github.javiewer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.github.javiewer.network.Network;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Author: MagicDroidX
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
                .baseUrl("https://avmo.pw")
                .build();

        Network network = retrofit.create(Network.class);

        return network.query(this.query, page);
    }
}
