package cz.zemankrystof.izastavka.data.model.regiojet;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RJStation {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("delay")
    @Expose
    private Integer delay;
    @SerializedName("virtual")
    @Expose
    private Boolean virtual;
    @SerializedName("freeSeatsCount")
    @Expose
    private Integer freeSeatsCount;
    @SerializedName("connectionStations")
    @Expose
    private List<RJConnectionStation> connectionStations = null;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Boolean getVirtual() {
        return virtual;
    }

    public void setVirtual(Boolean virtual) {
        this.virtual = virtual;
    }

    public Integer getFreeSeatsCount() {
        return freeSeatsCount;
    }

    public void setFreeSeatsCount(Integer freeSeatsCount) {
        this.freeSeatsCount = freeSeatsCount;
    }

    public List<RJConnectionStation> getConnectionStations() {
        return connectionStations;
    }

    public void setConnectionStations(List<RJConnectionStation> connectionStations) {
        this.connectionStations = connectionStations;
    }

}