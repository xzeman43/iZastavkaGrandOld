package cz.zemankrystof.izastavka.data.remote;

public class ApiUtils {

    public static final String BASE_URL = "https://dpmbinfo.dpmb.cz/api/";

    public static FlixService getFlixService() {
        return RetrofitClient.getClient().create(FlixService.class);
    }

    public static RJService getRJService(){
        return RetrofitClient.getRJClient().create(RJService.class);
    }

    public static BannerService getBannerService(){
        return RetrofitClient.getBannerClient().create(BannerService.class);
    }
}
