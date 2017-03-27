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

    public final static int STATE_NEW = 0;
    public final static int STATE_NOT_FINISHED = STATE_NEW + 1;
    public final static int STATE_IS_FINISHED = STATE_NOT_FINISHED + 1;

    private String key;
    @SerializedName("baliseDtoList")
    private List<Balise> baliseList;
    /**
     * This is the index of the next balise to get.
     * Use to save progression.
     */
    private int baliseToTarget = 0;
    private long elapsedTimeMillis;
    private int state;
    private int tries;

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
        this.state = STATE_NEW;
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
        Log.d(LOG, "getBaliseToTargetIndex " + baliseToTarget);
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

    /**
     * Increments the target balise of possible
     *
     * @return if target balise is NOT the last one
     */
    public boolean incrementTargetBalise() {
        if (baliseToTarget < baliseList.size() - 1) {
            baliseToTarget++;
            Log.d(LOG, "incremented target balise to index: " + baliseToTarget);
            return true;
        }
        return false;
    }

    public boolean isBaliseLastOne(int baliseId) {
        return getBaliseList().get(baliseList.size() - 1).getId() == baliseId;
    }

    public boolean isPrimarybalise(int baliseId) {
        return getBaliseList().get(0).getId() == baliseId;
    }

    public int getNumberOfBalises() {
        return baliseList.size();
    }

    public int getBaliseIndex(int id) {
        for (int i = 0; i < baliseList.size(); i++) {
            if (baliseList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    /**
     * Resets the target balise and elapsed time to 0;
     * Increments the number of tries.
     */
    public void resetProgression() {
        baliseToTarget = 1;
        elapsedTimeMillis = 0;
        tries++;
    }

    public int getTries() {
        return tries;
    }

    public String getStateName() {
        switch (state) {
            case STATE_NEW:
                return "STATE_NEW";
            case STATE_NOT_FINISHED:
                return "STATE_NOT_FINSIHED";
            case STATE_IS_FINISHED:
                return "STATE_IS_FINISHED";
        }
        return null;
    }
}
