package cz.zemankrystof.izastavka.data.model.LinkSchema;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinkSchemas {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("DayLines")
    @Expose
    private DayLines dayLines;
    @SerializedName("CentrumLines")
    @Expose
    private CentrumLines centrumLines;
    @SerializedName("NightLines")
    @Expose
    private NightLines nightLines;
    @SerializedName("TrafficHubs")
    @Expose
    private List<TrafficHub> trafficHubs = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DayLines getDayLines() {
        return dayLines;
    }

    public void setDayLines(DayLines dayLines) {
        this.dayLines = dayLines;
    }

    public CentrumLines getCentrumLines() {
        return centrumLines;
    }

    public void setCentrumLines(CentrumLines centrumLines) {
        this.centrumLines = centrumLines;
    }

    public NightLines getNightLines() {
        return nightLines;
    }

    public void setNightLines(NightLines nightLines) {
        this.nightLines = nightLines;
    }

    public List<TrafficHub> getTrafficHubs() {
        return trafficHubs;
    }

    public void setTrafficHubs(List<TrafficHub> trafficHubs) {
        this.trafficHubs = trafficHubs;
    }

}