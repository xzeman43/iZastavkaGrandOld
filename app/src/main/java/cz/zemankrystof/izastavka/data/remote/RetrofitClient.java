package cz.zemankrystof.izastavka.data.remote;

import cz.zemankrystof.izastavka.helpers.UnsafeOkHttpClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static Retrofit rjRetrofit;
    private static Retrofit bannerRetrofit;
    public static final String BASE_URL = "https://api.flixbus.com/public/v1/network/station/11918/";
    public static final String RJ_URL = "https://brn-ybus-pubapi.sa.cz/restapi/routes/10204002/";
    public static final String BANNER_URL = "http://10.0.230.1/images/";

    public static Retrofit getClient() {
        if (retrofit==null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

//    public static Retrofit getRJClient() {
//        if (rjRetrofit==null) {
//            rjRetrofit = new retrofit2.Retrofit.Builder()
//                    .baseUrl(RJ_URL)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//        }
//        return rjRetrofit;
//    }

    public static Retrofit getRJClient(){
        OkHttpClient okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(RJ_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        return retrofit;
    }

    public static Retrofit getBannerClient() {
        if (bannerRetrofit==null) {
            bannerRetrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BANNER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return bannerRetrofit;
    }
}
