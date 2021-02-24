package cz.zemankrystof.izastavka.data.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostList {

    @SerializedName("PostID")
    @Expose
    private Integer postID;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Departures")
    @Expose
    private List<Departure> departures = null;

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Departure> getDepartures() {
        return departures;
    }

    public void setDepartures(List<Departure> departures) {
        this.departures = departures;
    }

}