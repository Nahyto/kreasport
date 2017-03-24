package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Master on 23/03/2017.
 */

public class BaseItem {

    @SerializedName("name")
    protected String title;
    protected int id;

    public BaseItem(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
