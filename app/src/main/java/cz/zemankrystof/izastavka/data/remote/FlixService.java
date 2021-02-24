package cz.zemankrystof.izastavka.data.remote;

import java.util.List;

import cz.zemankrystof.izastavka.data.model.flix.Flix;
import cz.zemankrystof.izastavka.data.model.regiojet.RJStation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface FlixService {
    @GET("timetable.json")
    Call<Flix> getDepartures(@Header("X-API-Authentication") String apiKey, @Header("Accept-Language") String lang);
}
