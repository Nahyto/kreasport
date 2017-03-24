package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Parcours extends BaseItem {

    private String description;
    private String key;
    @SerializedName("baliseDtoList")
    private List<Balise> baliseList;

    public Parcours(String title) {
        super(title);
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<Balise> getBaliseList() {
        return baliseList;
    }

    public void setBaliseList(List<Balise> baliseList) {
        this.baliseList = baliseList;
    }

    public Balise getPrimaryBalise() {
        return baliseList.get(0);
    }

}
