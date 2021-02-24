

package cz.zemankrystof.izastavka.data.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StopList {

    @SerializedName("StopID")
    @Expose
    private Integer stopID;
    @SerializedName("Zone")
    @Expose
    private Integer zone;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("IsPublic")
    @Expose
    private Boolean isPublic;

    public Integer getStopID() {
        return stopID;
    }

    public void setStopID(Integer stopID) {
        this.stopID = stopID;
    }

    public Integer getZone() {
        return zone;
    }

    public void setZone(Integer zone) {
        this.zone = zone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

}