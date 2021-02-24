package cz.zemankrystof.izastavka.data.model.flix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Timetable {

    @SerializedName("arrivals")
    @Expose
    private List<Arrival> arrivals = null;
    @SerializedName("departures")
    @Expose
    private List<FlixDeparture> flixDepartures = null;
    @SerializedName("message")
    @Expose
    private String message;

    public List<Arrival> getArrivals() {
        return arrivals;
    }

    public void setArrivals(List<Arrival> arrivals) {
        this.arrivals = arrivals;
    }

    public List<FlixDeparture> getFlixDepartures() {
        return flixDepartures;
    }

    public void setFlixDepartures(List<FlixDeparture> flixDepartures) {
        this.flixDepartures = flixDepartures;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}