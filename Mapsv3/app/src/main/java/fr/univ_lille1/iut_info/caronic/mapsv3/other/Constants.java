package fr.univ_lille1.iut_info.caronic.mapsv3.other;

import android.content.Context;

import org.osmdroid.util.GeoPoint;

/**
 * Created by Master on 19/03/2017.
 */

public class Constants {

    public static final int TILE_KB_SIZE = 15;

    public static final int DOWNLOAD_MIN_ZOOM = 11;
    public static final int DOWNLOAD_MAX_ZOOM = 17;
    public static final String ACTION_PROXIMITY_ALERT = "fr.univ_lille1.iut_info.caronic.mapsv3.other.YourReceiver";

    public static final GeoPoint LOCATION_IUT = new GeoPoint(50.613608, 3.137074);

    public static final long MINIMUM_TIME_BETWEEN_UPDATE = 5000;
    public static final float MINIMUM_DISTANCECHANGE_FOR_UPDATE = 3;

    /**
     * The radius of the circle the user has to be in to activate a parcours in meters.
     * Default = 5
     */
    public static final double MAXIMUM_DISTANCE_TO_ACTIVATE_PARCOURS = 90;
}
