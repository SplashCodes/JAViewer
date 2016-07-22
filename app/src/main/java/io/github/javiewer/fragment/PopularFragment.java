package io.github.javiewer.fragment;

import io.github.javiewer.network.AVMO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Project: JAViewer
 */
public class PopularFragment extends MovieFragment {
    @Override
    public Call<ResponseBody> getCall(int page) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AVMO.BASE_URL)
                .build();

        AVMO avmo = retrofit.create(AVMO.class);

        return avmo.getPopular(page);
    }
}
