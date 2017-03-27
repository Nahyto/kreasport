package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Master on 23/03/2017.
 */

public class BaseItem {

    @SerializedName("name")
    protected String title;
    protected int id;
    protected String description;

    public BaseItem(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public BaseItem(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BaseItem{" +
                "title='" + title + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
