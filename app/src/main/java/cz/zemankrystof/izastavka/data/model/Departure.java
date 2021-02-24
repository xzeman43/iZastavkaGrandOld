package cz.zemankrystof.izastavka.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Departure {

    @SerializedName("Line")
    @Expose
    private String line;
    @SerializedName("FinalStop")
    @Expose
    private String finalStop;
    @SerializedName("IsLowFloor")
    @Expose
    private Boolean isLowFloor;
    @SerializedName("TimeMark")
    @Expose
    private String timeMark;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getFinalStop() {
        return finalStop;
    }

    public void setFinalStop(String finalStop) {
        this.finalStop = finalStop;
    }

    public Boolean getIsLowFloor() {
        return isLowFloor;
    }

    public void setIsLowFloor(Boolean isLowFloor) {
        this.isLowFloor = isLowFloor;
    }

    public String getTimeMark() {
        return timeMark;
    }

    public void setTimeMark(String timeMark) {
        this.timeMark = timeMark;
    }

}