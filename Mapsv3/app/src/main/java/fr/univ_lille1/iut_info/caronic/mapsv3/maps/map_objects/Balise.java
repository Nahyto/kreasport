package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import android.location.Location;
import android.support.annotation.NonNull;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Balise extends BaseItem implements Comparable<Balise> {

    private double latitude;
    private double longitude;
    private int parcoursId;
    private boolean isPrimaryBalise;
    private String question;
    private List<String> answers;

    public Balise(String title, String description, double latitude, double longitude, int parcoursId, int id) {
        super(title, description);
        this.latitude = latitude;
        this.longitude = longitude;
        this.parcoursId = parcoursId;
        this.id = id;
    }

    public Balise(String title, String description, GeoPoint point, int parcoursId, int id) {
        super(title, description);
        this.description = description;
        this.latitude = point.getLatitude();
        this.longitude = point.getLongitude();
        this.parcoursId = parcoursId;
        this.id = id;
    }

    public Balise(String title, double latitude, double longitude, int parcoursId, int id) {
        super(title, "No description");
        this.latitude = latitude;
        this.longitude = longitude;
        this.parcoursId = parcoursId;
        this.id = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getParcoursId() {
        return parcoursId;
    }

    public void setParcoursId(int parcoursId) {
        this.parcoursId = parcoursId;
    }

    public boolean isPrimaryBalise() {
        return isPrimaryBalise;
    }

    public void setPrimaryBalise(boolean primaryBalise) {
        isPrimaryBalise = primaryBalise;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public GeoPoint toGeoPoint() {
        return new GeoPoint(latitude, longitude);
    }

    public Location toLocation() {
        Location result = new Location("");
        result.setLatitude(getLatitude());
        result.setLongitude(getLongitude());
        return result;
    }

    @Override
    public int compareTo(@NonNull Balise o) {
        if (id > o.getParcoursId()) {
            return 1;
        } else if (id < o.getParcoursId()) {
            return -1;
        }
        return 0;
    }

    public CustomOverlayItem toCustomOverlayItem() {
        return new CustomOverlayItem(title, description, new GeoPoint(latitude, longitude), parcoursId, id);
    }
}
