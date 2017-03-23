package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

/**
 * Created by Chris on 23-Mar-17.
 */

public class Balise {

    private double longitude;
    private double latitude;
    private int parcours;


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
