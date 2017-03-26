package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Parcours extends BaseItem {

    private String key;
    @SerializedName("baliseDtoList")
    private List<Balise> baliseList;

    public Parcours(String title, String description, int id) {
        super(title, description, id);
        baliseList = new ArrayList<>();
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

    public void addBalise(Balise balise) {
        baliseList.add(balise);
    }

    @Override
    public String toString() {
        return "Parcours{" +
                "key='" + key + '\'' +
                super.toString() + '\'' +
                '}';
    }

    public Balise getBaliseById(int baliseId) {
        for (Balise balise : baliseList) {
            if (balise.getId() == baliseId) {
                return balise;
            }
        }
        return null;
    }
}
