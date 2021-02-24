package cz.zemankrystof.izastavka.data.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SurroundingVeh {

    @SerializedName("Version")
    @Expose
    private Integer version;
    @SerializedName("UpdateInterval")
    @Expose
    private Integer updateInterval;
    @SerializedName("LastUpdate")
    @Expose
    private String lastUpdate;
    @SerializedName("Data")
    @Expose
    private List<Datum> data = null;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(Integer updateInterval) {
        this.updateInterval = updateInterval;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}