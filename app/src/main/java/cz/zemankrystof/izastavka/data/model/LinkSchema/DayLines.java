package cz.zemankrystof.izastavka.data.model.LinkSchema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DayLines extends RealmObject{

    @PrimaryKey
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ImageURL")
    @Expose
    private String imageURL;
    @SerializedName("imageLocation")
    @Expose
    private String imageLocation;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }


    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String imageLocation) {
        this.imageLocation = imageLocation;
    }

}