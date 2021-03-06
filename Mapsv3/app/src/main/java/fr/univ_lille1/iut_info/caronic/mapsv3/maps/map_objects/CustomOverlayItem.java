package fr.univ_lille1.iut_info.caronic.mapsv3.maps.map_objects;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

/**
 * Created by Master on 25/03/2017.
 */

public class CustomOverlayItem extends OverlayItem {

    private int parcoursId;
    private int id;

    public CustomOverlayItem(String aTitle, String aSnippet, IGeoPoint aGeoPoint) {
        super(aTitle, aSnippet, aGeoPoint);
    }

    public CustomOverlayItem(String aTitle, String aSnippet, IGeoPoint aGeoPoint, int parcoursId, int id) {
        super(aTitle, aSnippet, aGeoPoint);
        this.parcoursId = parcoursId;
        this.id = id;
    }

    public int getParcoursId() {
        return parcoursId;
    }

    public void setParcoursId(int parcoursId) {
        this.parcoursId = parcoursId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
