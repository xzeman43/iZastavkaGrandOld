package cz.zemankrystof.izastavka.data.model.regiojet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RJConnectionStation {

    @SerializedName("stationId")
    @Expose
    private Double stationId;
    @SerializedName("arrival")
    @Expose
    private String arrival;
    @SerializedName("departure")
    @Expose
    private String departure;
    @SerializedName("platform")
    @Expose
    private String platform;
    @SerializedName("departingStation")
    @Expose
    private Boolean departingStation;

    public Double getStationId() {
        return stationId;
    }

    public void setStationId(Double stationId) {
        this.stationId = stationId;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Boolean getDepartingStation() {
        return departingStation;
    }

    public void setDepartingStation(Boolean departingStation) {
        this.departingStation = departingStation;
    }

}