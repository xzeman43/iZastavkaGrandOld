package cz.zemankrystof.izastavka.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("VType")
    @Expose
    private Integer vType;
    @SerializedName("LType")
    @Expose
    private Integer lType;
    @SerializedName("Lat")
    @Expose
    private Double lat;
    @SerializedName("Lng")
    @Expose
    private Double lng;
    @SerializedName("LineID")
    @Expose
    private Integer lineID;
    @SerializedName("RouteID")
    @Expose
    private Integer routeID;
    @SerializedName("Course")
    @Expose
    private String course;
    @SerializedName("Delay")
    @Expose
    private Integer delay;
    @SerializedName("Bearing")
    @Expose
    private Integer bearing;
    @SerializedName("LastStopID")
    @Expose
    private Integer lastStopID;
    @SerializedName("FinalStopID")
    @Expose
    private Integer finalStopID;
    @SerializedName("LF")
    @Expose
    private Boolean lF;
    @SerializedName("LineName")
    @Expose
    private String lineName;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public Integer getVType() {
        return vType;
    }

    public void setVType(Integer vType) {
        this.vType = vType;
    }

    public Integer getLType() {
        return lType;
    }

    public void setLType(Integer lType) {
        this.lType = lType;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getLineID() {
        return lineID;
    }

    public void setLineID(Integer lineID) {
        this.lineID = lineID;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getBearing() {
        return bearing;
    }

    public void setBearing(Integer bearing) {
        this.bearing = bearing;
    }

    public Integer getLastStopID() {
        return lastStopID;
    }

    public void setLastStopID(Integer lastStopID) {
        this.lastStopID = lastStopID;
    }

    public Integer getFinalStopID() {
        return finalStopID;
    }

    public void setFinalStopID(Integer finalStopID) {
        this.finalStopID = finalStopID;
    }

    public Boolean getLF() {
        return lF;
    }

    public void setLF(Boolean lF) {
        this.lF = lF;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

}