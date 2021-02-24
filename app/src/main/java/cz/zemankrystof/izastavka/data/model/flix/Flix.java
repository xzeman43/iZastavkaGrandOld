package cz.zemankrystof.izastavka.data.model.flix;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Flix {

    @SerializedName("timetable")
    @Expose
    private Timetable timetable;
    @SerializedName("station")
    @Expose
    private Station station;

    public Timetable getTimetable() {
        return timetable;
    }

    public void setTimetable(Timetable timetable) {
        this.timetable = timetable;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

}