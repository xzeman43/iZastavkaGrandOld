package cz.zemankrystof.izastavka.data.model.StopTimetables;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Timetable extends RealmObject{

    @PrimaryKey
    @SerializedName("TimetableName")
    @Expose
    private String timetableName;
    @SerializedName("TimetableFileName")
    @Expose
    private String timetableFileName;
    @SerializedName("TimetableURL")
    @Expose
    private String timetableURL;
    @SerializedName("timetableImageLocation")
    @Expose
    private String timetableImageLocation;

    public String getTimetableName() {
        return timetableName;
    }

    public void setTimetableName(String timetableName) {
        this.timetableName = timetableName;
    }

    public String getTimetableFileName() {
        return timetableFileName;
    }

    public void setTimetableFileName(String timetableFileName) {
        this.timetableFileName = timetableFileName;
    }

    public String getTimetableURL() {
        return timetableURL;
    }

    public void setTimetableURL(String timetableURL) {
        this.timetableURL = timetableURL;
    }

    public String getTimetableImageLocation() {
        return timetableImageLocation;
    }

    public void setTimetableImageLocation(String timetableImageLocation) {
        this.timetableImageLocation = timetableImageLocation;
    }

}
