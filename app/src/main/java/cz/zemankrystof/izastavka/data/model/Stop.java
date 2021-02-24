package cz.zemankrystof.izastavka.data.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stop {

    @SerializedName("StopID")
    @Expose
    private Integer stopID;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("PostList")
    @Expose
    private List<PostList> postList = null;

    public Integer getStopID() {
        return stopID;
    }

    public void setStopID(Integer stopID) {
        this.stopID = stopID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<PostList> getPostList() {
        return postList;
    }

    public void setPostList(List<PostList> postList) {
        this.postList = postList;
    }
}