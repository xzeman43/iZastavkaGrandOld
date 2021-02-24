package cz.zemankrystof.izastavka.data.remote;

import cz.zemankrystof.izastavka.data.model.LinkSchema.LinkSchemas;
import cz.zemankrystof.izastavka.data.model.Stop;
import cz.zemankrystof.izastavka.data.model.StopTimetables.StopTimetables;
import cz.zemankrystof.izastavka.data.model.SurroundingVeh;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StopsService {

    @GET("Departures")
    Call<Stop> getStops(@Query("stopid") int stopid, @Query("postid") int postid);

    @GET("smart_{stopid}.json")
    Call<SurroundingVeh> getVehicles(@Path("stopid") int stopid);

    @GET("Schema")
    Call<LinkSchemas> getSchemas();

    @GET("Timetables")
    Call<StopTimetables> getTimetables(@Query("stopid") int stopid, @Query("postid") int postid);

}
