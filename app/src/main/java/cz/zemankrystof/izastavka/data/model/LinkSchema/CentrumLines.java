package cz.zemankrystof.izastavka.data.model.LinkSchema;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CentrumLines {

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("ImageURL")
    @Expose
    private String imageURL;

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

}