package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Parcours extends BaseItem {

    private final static String LOG = Parcours.class.getSimpleName();

    private String key;
    @SerializedName("baliseDtoList")
    private List<Balise> baliseList;
    /**
     * This is the index of the next balise to get.
     * Use to save progression.
     */
    private int baliseToTarget = 0;
    private long elapsedTimeMillis;

    public Parcours(String title, String description, int id) {
        super(title, description, id);
        this.elapsedTimeMillis = 0;
        baliseList = new ArrayList<Balise>() {
            public boolean add(Balise balise) {
                super.add(balise);
                Collections.sort(baliseList); // Balise implements Comparable on id so they'll be sorted by that automatically
                return true;
            }
        };
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

    /**
     * <b>!! Use exclusively when getting the balises sequentially. !!</b>
     *
     * @return the next balise from last time this was called. Starts at 0.
     */
    public Balise getNextBalise() {
        if (baliseToTarget < baliseList.size()) {
            // return and then increment
            return baliseList.get(baliseToTarget++);
        }
        return null;
    }

    /**
     * The index of the balise to target.
     *
     * @return
     */
    public int getBaliseToTargetIndex() {
        return baliseToTarget;
    }

    public Balise getBaliseToTarget() {
        return baliseList.get(baliseToTarget);
    }

    public void setBaliseToTarget(int baliseToTarget) {
        this.baliseToTarget = baliseToTarget;
    }

    public void setElapsedTimeMillis(long elapsedTimeMillis) {
        this.elapsedTimeMillis = elapsedTimeMillis;
        Log.d(LOG, "updated current time for parcours: " + id + " -> " + elapsedTimeMillis);
    }

    public long getElapsedTimeMillis() {
        Log.d(LOG, "getting time for parcours: " + id + " -> " + elapsedTimeMillis);
        return elapsedTimeMillis;
    }

    public void incrementTargetBalise() {
        baliseToTarget++;
    }
}
