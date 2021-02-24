package cz.zemankrystof.izastavka.data.remote;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface BannerService {

    @GET("list.txt")
    Call<ResponseBody> getBannerList();
}
