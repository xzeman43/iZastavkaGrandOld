package cz.zemankrystof.izastavka.data.model.StopTimetables;

import android.util.Log;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class StopTimetables extends RealmObject{

    @PrimaryKey
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("LastModified")
    @Expose
    private double lastModified;
    @SerializedName("Timetables")
    @Expose
    private RealmList<Timetable> timetables = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getLastModified() {
        return lastModified;
    }

    public void setLastModified(double lastModified) {
        this.lastModified = lastModified;
    }

    public List<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetables(RealmList<Timetable> timetables) {
        this.timetables = timetables;
    }

}
