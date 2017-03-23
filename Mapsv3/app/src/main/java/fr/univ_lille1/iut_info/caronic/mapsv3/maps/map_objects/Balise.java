package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Balise extends BaseItem {

    private double latitude;
    private double longitude;
    private int parcours;

    public Balise(String title, double latitude, double longitude, int parcours) {
        super(title);
        this.latitude = latitude;
        this.longitude = longitude;
        this.parcours = parcours;
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

    public int getParcours() {
        return parcours;
    }

    public void setParcours(int parcours) {
        this.parcours = parcours;
    }
}
