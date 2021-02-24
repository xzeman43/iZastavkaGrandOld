package cz.zemankrystof.izastavka.data.model.flix;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Arrival {

    @SerializedName("through_the_stations")
    @Expose
    private String throughTheStations;
    @SerializedName("datetime")
    @Expose
    private Datetime datetime;
    @SerializedName("line_direction")
    @Expose
    private String lineDirection;
    @SerializedName("ride_id")
    @Expose
    private Integer rideId;
    @SerializedName("trip_uid")
    @Expose
    private String tripUid;
    @SerializedName("route")
    @Expose
    private List<Route> route = null;
    @SerializedName("line_code")
    @Expose
    private String lineCode;
    @SerializedName("direction")
    @Expose
    private String direction;
    @SerializedName("delay_status")
    @Expose
    private String delayStatus;
    @SerializedName("delay")
    @Expose
    private Delay delay;
    @SerializedName("station_ride_message")
    @Expose
    private String stationRideMessage;

    public String getThroughTheStations() {
        return throughTheStations;
    }

    public void setThroughTheStations(String throughTheStations) {
        this.throughTheStations = throughTheStations;
    }

    public Datetime getDatetime() {
        return datetime;
    }

    public void setDatetime(Datetime datetime) {
        this.datetime = datetime;
    }

    public String getLineDirection() {
        return lineDirection;
    }

    public void setLineDirection(String lineDirection) {
        this.lineDirection = lineDirection;
    }

    public Integer getRideId() {
        return rideId;
    }

    public void setRideId(Integer rideId) {
        this.rideId = rideId;
    }

    public String getTripUid() {
        return tripUid;
    }

    public void setTripUid(String tripUid) {
        this.tripUid = tripUid;
    }

    public List<Route> getRoute() {
        return route;
    }

    public void setRoute(List<Route> route) {
        this.route = route;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getDelayStatus() {
        return delayStatus;
    }

    public void setDelayStatus(String delayStatus) {
        this.delayStatus = delayStatus;
    }

    public Delay getDelay() {
        return delay;
    }

    public void setDelay(Delay delay) {
        this.delay = delay;
    }

    public String getStationRideMessage() {
        return stationRideMessage;
    }

    public void setStationRideMessage(String stationRideMessage) {
        this.stationRideMessage = stationRideMessage;
    }

}